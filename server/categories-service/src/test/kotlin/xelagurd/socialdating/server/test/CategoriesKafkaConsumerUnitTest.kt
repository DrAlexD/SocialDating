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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails
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

    private val userCategory = FakeCategoriesData.userCategory
    private val userCategorySlot = slot<UserCategory>()
    private val updateDetails = mockk<UserCategoryUpdateDetails>(relaxed = true)

    @Test
    fun updateUserCategory_existData() {
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
    fun updateUserCategory_noData() {
        every { userCategoriesService.getUserCategory(any(), any()) } returns null

        every { userCategoriesService.addUserCategory(capture(userCategorySlot)) } returns mockk()

        categoriesKafkaConsumer.updateUserCategory(updateDetails)

        assertEquals(CATEGORY_INTEREST_STEP, userCategorySlot.captured.interest)

        verify(exactly = 1) { userCategoriesService.getUserCategory(any(), any()) }
        verify(exactly = 1) { userCategoriesService.addUserCategory(any()) }
        verify(exactly = 1) { categoriesKafkaProducer.updateUserDefiningTheme(any()) }
        confirmVerified(userCategoriesService, categoriesKafkaProducer)
    }

    @Disabled
    @Test
    fun updateMaintainedList() {
        // TODO
    }

}