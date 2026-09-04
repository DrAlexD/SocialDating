package xelagurd.socialdating.server.test

import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.common.CategoryUpdateDetails
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.common.MaintainedListUpdate
import xelagurd.socialdating.server.model.common.UserCategoriesUpdateDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.NOT_SURE
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_NO_MAINTAIN
import xelagurd.socialdating.server.service.DefiningThemesKafkaConsumer
import xelagurd.socialdating.server.service.DefiningThemesKafkaProducer
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.service.UserDefiningThemesService

@ExtendWith(MockKExtension::class)
class DefiningThemesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    @MockK
    private lateinit var definingThemesService: DefiningThemesService

    @MockK(relaxed = true)
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    private val definingTheme = FakeDefiningThemesData.definingThemeResponses[0]
    private val baseUserDefiningTheme = FakeDefiningThemesData.userDefiningThemes[0]

    private val anotherCategoryDefiningTheme = FakeDefiningThemesData.definingThemeResponses[7]
    private val anotherCategoryUserDefiningTheme = FakeDefiningThemesData.userDefiningThemes[2]

    private val userDefiningThemeSlot = slot<UserDefiningTheme>()
    private val updateDetailsSlot = slot<UserCategoriesUpdateDetails>()

    private fun userDefiningTheme(value: Int) = baseUserDefiningTheme.copy(value = value)

    private fun updateDetails(
        reactionType: StatementReactionType,
        isSupportDefiningTheme: Boolean = true
    ) = UserDefiningThemesUpdateDetails(
        userId = baseUserDefiningTheme.userId,
        reactionType = reactionType,
        definingThemes = listOf(
            DefiningThemeReactionDetails(baseUserDefiningTheme.definingThemeId, isSupportDefiningTheme)
        )
    )

    private fun mockGetUserDefiningTheme(userDefiningTheme: UserDefiningTheme?) {
        every { definingThemesService.getDefiningThemes(any(), any()) } returns listOf(definingTheme)
        every { userDefiningThemesService.getUserDefiningTheme(any(), any()) } returns userDefiningTheme
        every { userDefiningThemesService.addUserDefiningTheme(capture(userDefiningThemeSlot)) } returns mockk()
        every { definingThemesKafkaProducer.updateUserCategories(capture(updateDetailsSlot)) } just Runs
    }

    private fun verifySingleThemeProcessed() {
        verify(exactly = 1) { definingThemesService.getDefiningThemes(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateUserCategories(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningThemes_fullNoMaintain_decreaseMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))

        definingThemesKafkaConsumer.updateUserDefiningThemes(FakeDefiningThemesData.userDefiningThemesUpdateDetails)

        assertEquals(75, userDefiningThemeSlot.captured.value)
        assertEquals(
            baseUserDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP,
            userDefiningThemeSlot.captured.interest
        )

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_fullMaintain_increaseMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 75))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(FULL_MAINTAIN))

        assertEquals(85, userDefiningThemeSlot.captured.value)

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_fullNoMaintain_increaseNotMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 25))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(15, userDefiningThemeSlot.captured.value)

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_fullMaintain_decreaseNotMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 15))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(FULL_MAINTAIN))

        assertEquals(25, userDefiningThemeSlot.captured.value)

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_partNoMaintain_noBorderCrossingButStillUpdatesCategory() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(PART_NO_MAINTAIN))

        assertEquals(80, userDefiningThemeSlot.captured.value)
        assertEquals(
            listOf(CategoryUpdateDetails(definingTheme.categoryId)),
            updateDetailsSlot.captured.categories
        )

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_partMaintain_noBorderCrossing() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 50))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(PART_MAINTAIN))

        assertEquals(55, userDefiningThemeSlot.captured.value)
        assertEquals(
            listOf<MaintainedListUpdate>(),
            updateDetailsSlot.captured.categories[0].maintainedListUpdates
        )

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_notSure_changesValueWithinRandomRange() {
        val initialValue = 50
        mockGetUserDefiningTheme(userDefiningTheme(value = initialValue))

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(NOT_SURE))

        assertTrue(userDefiningThemeSlot.captured.value in (initialValue - 1)..(initialValue + 1))

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_notSupportDefiningTheme_invertsDiff() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 50))

        definingThemesKafkaConsumer.updateUserDefiningThemes(
            updateDetails(FULL_MAINTAIN, isSupportDefiningTheme = false)
        )

        assertEquals(40, userDefiningThemeSlot.captured.value)

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_noData_createsNewWithInitialValue() {
        mockGetUserDefiningTheme(null)

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(DEFINING_THEME_VALUE_INITIAL - 10, userDefiningThemeSlot.captured.value)
        assertEquals(DEFINING_THEME_INTEREST_STEP, userDefiningThemeSlot.captured.interest)
        assertEquals(
            listOf(CategoryUpdateDetails(definingTheme.categoryId)),
            updateDetailsSlot.captured.categories
        )

        verifySingleThemeProcessed()
    }

    @Test
    fun updateUserDefiningThemes_themeNotFound_updatesValueWithoutProducingEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))
        every { definingThemesService.getDefiningThemes(any(), any()) } returns listOf()

        definingThemesKafkaConsumer.updateUserDefiningThemes(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(75, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { definingThemesService.getDefiningThemes(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningThemes_themesOfSeveralCategories_producesSingleEventWithEveryCategory() {
        val updatedUserDefiningThemes = mutableListOf<UserDefiningTheme>()

        every { definingThemesService.getDefiningThemes(any(), any()) } returns
                listOf(definingTheme, anotherCategoryDefiningTheme)
        every {
            userDefiningThemesService.getUserDefiningTheme(any(), definingTheme.id!!)
        } returns userDefiningTheme(value = 85)
        every {
            userDefiningThemesService.getUserDefiningTheme(any(), anotherCategoryDefiningTheme.id!!)
        } returns anotherCategoryUserDefiningTheme
        every {
            userDefiningThemesService.addUserDefiningTheme(capture(updatedUserDefiningThemes))
        } returns mockk()
        every { definingThemesKafkaProducer.updateUserCategories(capture(updateDetailsSlot)) } just Runs

        definingThemesKafkaConsumer.updateUserDefiningThemes(
            UserDefiningThemesUpdateDetails(
                userId = baseUserDefiningTheme.userId,
                reactionType = FULL_NO_MAINTAIN,
                definingThemes = listOf(
                    DefiningThemeReactionDetails(definingTheme.id!!, true),
                    DefiningThemeReactionDetails(anotherCategoryDefiningTheme.id!!, true)
                )
            )
        )

        assertEquals(listOf(75, 0), updatedUserDefiningThemes.map { it.value })
        assertEquals(
            listOf(
                // the border is crossed only in the first category
                CategoryUpdateDetails(
                    categoryId = definingTheme.categoryId,
                    maintainedListUpdates = listOf(
                        MaintainedListUpdate(DECREASE_MAINTAINED, definingTheme.numberInCategory)
                    )
                ),
                CategoryUpdateDetails(categoryId = anotherCategoryDefiningTheme.categoryId)
            ),
            updateDetailsSlot.captured.categories
        )

        verify(exactly = 1) { definingThemesService.getDefiningThemes(any(), any()) }
        verify(exactly = 2) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 2) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateUserCategories(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }
}
