package xelagurd.socialdating.server.test

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.common.CategoryUpdateDetails
import xelagurd.socialdating.server.model.common.MaintainedListUpdate
import xelagurd.socialdating.server.model.common.UserCategoriesUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.service.CategoriesKafkaConsumer
import xelagurd.socialdating.server.service.UserCategoriesService

@ExtendWith(MockKExtension::class)
class CategoriesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userCategoriesService: UserCategoriesService

    @InjectMockKs
    private lateinit var categoriesKafkaConsumer: CategoriesKafkaConsumer

    private val userCategory = FakeCategoriesData.userCategories[0]
    private val userCategorySlot = slot<UserCategory>()

    private fun userCategoryWith(
        maintained: Array<Long>? = null,
        notMaintained: Array<Long>? = null
    ) = UserCategory(
        id = 1,
        interest = 50,
        userId = 1,
        categoryId = 1,
        maintained = maintained,
        notMaintained = notMaintained
    )

    private fun updateDetails(vararg maintainedListUpdates: MaintainedListUpdate) =
        UserCategoriesUpdateDetails(
            userId = 1,
            categories = listOf(
                CategoryUpdateDetails(
                    categoryId = 1,
                    maintainedListUpdates = maintainedListUpdates.toList()
                )
            )
        )

    private fun maintainedListUpdate(
        updateType: MaintainedListUpdateType,
        numberInCategory: Int
    ) = MaintainedListUpdate(
        updateType = updateType,
        numberInCategory = numberInCategory
    )

    private fun mockGetUserCategory(userCategory: UserCategory?) {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategory
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()
    }

    private fun verifySingleCategoryProcessed() {
        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService)
    }

    @Test
    fun updateUserCategories_existData_increasesInterest() {
        mockGetUserCategory(userCategory)

        categoriesKafkaConsumer.updateUserCategories(updateDetails())

        assertEquals(userCategory.interest + CATEGORY_INTEREST_STEP, userCategorySlot.captured.interest)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_noData_createsUserCategory() {
        mockGetUserCategory(null)

        categoriesKafkaConsumer.updateUserCategories(updateDetails())

        assertEquals(CATEGORY_INTEREST_STEP, userCategorySlot.captured.interest)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_interestAtMax_coercesToMax() {
        mockGetUserCategory(userCategoryWith().copy(interest = 100))

        categoriesKafkaConsumer.updateUserCategories(updateDetails())

        assertEquals(100, userCategorySlot.captured.interest)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_increaseMaintained_setsBit() {
        mockGetUserCategory(userCategoryWith(maintained = null))

        // numberInCategory 3 -> index 2 -> bit 2 set -> 0b100 = 4
        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(maintainedListUpdate(INCREASE_MAINTAINED, numberInCategory = 3))
        )

        assertArrayEquals(arrayOf(4L), userCategorySlot.captured.maintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_decreaseMaintained_clearsBit() {
        mockGetUserCategory(userCategoryWith(maintained = arrayOf(0b0111L)))

        // numberInCategory 2 -> index 1 -> clear bit 1 -> 0b111 & ~0b010 = 0b101 = 5
        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(maintainedListUpdate(DECREASE_MAINTAINED, numberInCategory = 2))
        )

        assertArrayEquals(arrayOf(5L), userCategorySlot.captured.maintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_increaseNotMaintained_setsBit() {
        mockGetUserCategory(userCategoryWith(notMaintained = null))

        // numberInCategory 1 -> index 0 -> bit 0 set -> 1
        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(maintainedListUpdate(INCREASE_NOT_MAINTAINED, numberInCategory = 1))
        )

        assertArrayEquals(arrayOf(1L), userCategorySlot.captured.notMaintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_decreaseNotMaintained_clearsBit() {
        mockGetUserCategory(userCategoryWith(notMaintained = arrayOf(0b0011L)))

        // numberInCategory 1 -> index 0 -> clear bit 0 -> 0b11 & ~0b1 = 0b10 = 2
        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(maintainedListUpdate(DECREASE_NOT_MAINTAINED, numberInCategory = 1))
        )

        assertArrayEquals(arrayOf(2L), userCategorySlot.captured.notMaintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_highNumberInCategory_growsList() {
        mockGetUserCategory(userCategoryWith(maintained = arrayOf(1L)))

        // numberInCategory 70 -> index 69 -> listIndex 1, bitIndex 5 -> grows the list to size 2
        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(maintainedListUpdate(INCREASE_MAINTAINED, numberInCategory = 70))
        )

        assertArrayEquals(arrayOf(1L, 1L shl 5), userCategorySlot.captured.maintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_severalUpdatesOfOneCategory_appliesAllOfThemAtOnce() {
        mockGetUserCategory(userCategoryWith(maintained = arrayOf(0b0001L), notMaintained = null))

        categoriesKafkaConsumer.updateUserCategories(
            updateDetails(
                maintainedListUpdate(DECREASE_MAINTAINED, numberInCategory = 1),
                maintainedListUpdate(INCREASE_MAINTAINED, numberInCategory = 3),
                maintainedListUpdate(INCREASE_NOT_MAINTAINED, numberInCategory = 2)
            )
        )

        assertArrayEquals(arrayOf(0b0100L), userCategorySlot.captured.maintained)
        assertArrayEquals(arrayOf(0b0010L), userCategorySlot.captured.notMaintained)

        verifySingleCategoryProcessed()
    }

    @Test
    fun updateUserCategories_severalCategories_updatesEveryOfThem() {
        val updatedUserCategories = mutableListOf<UserCategory>()

        every { userCategoriesService.getUserCategory(any(), any()) } returns null
        every { userCategoriesService.addUserCategory(capture(updatedUserCategories)) } returns mockk()

        categoriesKafkaConsumer.updateUserCategories(
            UserCategoriesUpdateDetails(
                userId = 1,
                categories = listOf(
                    CategoryUpdateDetails(
                        categoryId = 1,
                        maintainedListUpdates = listOf(
                            maintainedListUpdate(INCREASE_MAINTAINED, numberInCategory = 1)
                        )
                    ),
                    CategoryUpdateDetails(categoryId = 2)
                )
            )
        )

        assertEquals(listOf(1, 2), updatedUserCategories.map { it.categoryId })
        assertArrayEquals(arrayOf(1L), updatedUserCategories[0].maintained)
        assertEquals(null, updatedUserCategories[1].maintained)

        verify(exactly = 2) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 2) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService)
    }
}
