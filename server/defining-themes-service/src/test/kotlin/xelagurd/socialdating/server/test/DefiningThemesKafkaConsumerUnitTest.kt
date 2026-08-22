package xelagurd.socialdating.server.test

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
import xelagurd.socialdating.server.model.common.UserDefiningThemeUpdateDetails
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

    @MockK(relaxed = true)
    private lateinit var definingThemesService: DefiningThemesService

    @MockK(relaxed = true)
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    private val baseUserDefiningTheme = FakeDefiningThemesData.userDefiningThemes[0]
    private val userDefiningThemeSlot = slot<UserDefiningTheme>()

    private fun userDefiningTheme(value: Int) = baseUserDefiningTheme.copy(value = value)

    private fun updateDetails(
        reactionType: StatementReactionType,
        isSupportDefiningTheme: Boolean = true
    ) = UserDefiningThemeUpdateDetails(
        userId = baseUserDefiningTheme.userId,
        definingThemeId = baseUserDefiningTheme.definingThemeId,
        reactionType = reactionType,
        isSupportDefiningTheme = isSupportDefiningTheme
    )

    private fun mockGetUserDefiningTheme(userDefiningTheme: UserDefiningTheme?) {
        every { userDefiningThemesService.getUserDefiningTheme(any(), any()) } returns userDefiningTheme
        every { userDefiningThemesService.addUserDefiningTheme(capture(userDefiningThemeSlot)) } returns mockk()
    }

    @Test
    fun updateUserDefiningTheme_fullNoMaintain_decreaseMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))

        definingThemesKafkaConsumer.updateUserDefiningTheme(FakeDefiningThemesData.userDefiningThemeUpdateDetails)

        assertEquals(75, userDefiningThemeSlot.captured.value)
        assertEquals(baseUserDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP, userDefiningThemeSlot.captured.interest)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateMaintainedList(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_fullMaintain_increaseMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 75))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_MAINTAIN))

        assertEquals(85, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateMaintainedList(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_fullNoMaintain_increaseNotMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 25))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(15, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateMaintainedList(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_fullMaintain_decreaseNotMaintainedAndProducesEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 15))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_MAINTAIN))

        assertEquals(25, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateMaintainedList(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_partNoMaintain_noBorderCrossingNoEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(PART_NO_MAINTAIN))

        assertEquals(80, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_partMaintain_noBorderCrossingNoEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 50))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(PART_MAINTAIN))

        assertEquals(55, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_notSure_changesValueWithinRandomRangeNoEvent() {
        val initialValue = 50
        mockGetUserDefiningTheme(userDefiningTheme(value = initialValue))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(NOT_SURE))

        assertTrue(userDefiningThemeSlot.captured.value in (initialValue - 1)..(initialValue + 1))

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_notSupportDefiningTheme_invertsDiff() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 50))

        definingThemesKafkaConsumer.updateUserDefiningTheme(
            updateDetails(FULL_MAINTAIN, isSupportDefiningTheme = false)
        )

        assertEquals(40, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_stayingNotMaintained_noEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 10))

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(0, userDefiningThemeSlot.captured.value)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_borderCrossedButThemeNotFound_doesNotProduceEvent() {
        mockGetUserDefiningTheme(userDefiningTheme(value = 85))
        every { definingThemesService.getDefiningTheme(any()) } returns null

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_NO_MAINTAIN))

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_noData_createsNewWithInitialValue() {
        mockGetUserDefiningTheme(null)

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails(FULL_NO_MAINTAIN))

        assertEquals(DEFINING_THEME_VALUE_INITIAL - 10, userDefiningThemeSlot.captured.value)
        assertEquals(DEFINING_THEME_INTEREST_STEP, userDefiningThemeSlot.captured.interest)

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }
}
