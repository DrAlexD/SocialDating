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
import xelagurd.socialdating.server.model.common.MaintainedListUpdateDetails
import xelagurd.socialdating.server.model.common.UserCategoryUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.service.CategoriesKafkaConsumer
import xelagurd.socialdating.server.service.CategoriesKafkaProducer
import xelagurd.socialdating.server.service.UserCategoriesService

@ExtendWith(MockKExtension::class)
class CategoriesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userCategoriesService: UserCategoriesService

    @MockK(relaxed = true)
    private lateinit var categoriesKafkaProducer: CategoriesKafkaProducer

    @InjectMockKs
    private lateinit var categoriesKafkaConsumer: CategoriesKafkaConsumer

    private val userCategory = FakeCategoriesData.userCategories[0]
    private val userCategorySlot = slot<UserCategory>()
    private val updateDetails = mockk<UserCategoryUpdateDetails>(relaxed = true)

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

    private fun maintainedListUpdateDetails(
        updateType: MaintainedListUpdateType,
        numberInCategory: Int
    ) = MaintainedListUpdateDetails(
        userId = 1,
        categoryId = 1,
        updateType = updateType,
        numberInCategory = numberInCategory
    )

    @Test
    fun updateUserCategory_existData_increasesInterestAndProducesEvent() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategory

        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        categoriesKafkaConsumer.updateUserCategory(updateDetails)

        assertEquals(userCategory.interest + CATEGORY_INTEREST_STEP, userCategorySlot.captured.interest)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        verify(exactly = 1) { categoriesKafkaProducer.updateUserDefiningTheme(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateUserCategory_noData_createsUserCategoryAndProducesEvent() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns null

        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        categoriesKafkaConsumer.updateUserCategory(updateDetails)

        assertEquals(CATEGORY_INTEREST_STEP, userCategorySlot.captured.interest)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        verify(exactly = 1) { categoriesKafkaProducer.updateUserDefiningTheme(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateUserCategory_interestAtMax_coercesToMax() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith().copy(interest = 100)

        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        categoriesKafkaConsumer.updateUserCategory(updateDetails)

        assertEquals(100, userCategorySlot.captured.interest)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        verify(exactly = 1) { categoriesKafkaProducer.updateUserDefiningTheme(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_increaseMaintained_setsBit() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith(maintained = null)
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        // numberInCategory 3 -> index 2 -> bit 2 set -> 0b100 = 4
        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(INCREASE_MAINTAINED, numberInCategory = 3)
        )

        assertArrayEquals(arrayOf(4L), userCategorySlot.captured.maintained)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_decreaseMaintained_clearsBit() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith(maintained = arrayOf(0b0111L))
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        // numberInCategory 2 -> index 1 -> clear bit 1 -> 0b111 & ~0b010 = 0b101 = 5
        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(DECREASE_MAINTAINED, numberInCategory = 2)
        )

        assertArrayEquals(arrayOf(5L), userCategorySlot.captured.maintained)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_increaseNotMaintained_setsBit() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith(notMaintained = null)
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        // numberInCategory 1 -> index 0 -> bit 0 set -> 1
        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(INCREASE_NOT_MAINTAINED, numberInCategory = 1)
        )

        assertArrayEquals(arrayOf(1L), userCategorySlot.captured.notMaintained)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_decreaseNotMaintained_clearsBit() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith(notMaintained = arrayOf(0b0011L))
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        // numberInCategory 1 -> index 0 -> clear bit 0 -> 0b11 & ~0b1 = 0b10 = 2
        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(DECREASE_NOT_MAINTAINED, numberInCategory = 1)
        )

        assertArrayEquals(arrayOf(2L), userCategorySlot.captured.notMaintained)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_highNumberInCategory_growsList() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns userCategoryWith(maintained = arrayOf(1L))
        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        // numberInCategory 70 -> index 69 -> listIndex 1, bitIndex 5 -> grows the list to size 2
        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(INCREASE_MAINTAINED, numberInCategory = 70)
        )

        assertArrayEquals(arrayOf(1L, 1L shl 5), userCategorySlot.captured.maintained)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Test
    fun updateMaintainedList_noUserCategory_doesNothing() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns null

        categoriesKafkaConsumer.updateMaintainedList(
            maintainedListUpdateDetails(INCREASE_MAINTAINED, numberInCategory = 1)
        )

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 0) { userCategoriesService.addUserCategory(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }
}
