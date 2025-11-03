package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.service.CategoriesKafkaConsumer
import xelagurd.socialdating.server.service.CategoriesKafkaProducer
import xelagurd.socialdating.server.service.UserCategoriesService

class CategoriesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userCategoriesService: UserCategoriesService

    @MockK
    private lateinit var kafkaProducer: CategoriesKafkaProducer

    @InjectMockKs
    private lateinit var categoriesKafkaConsumer: CategoriesKafkaConsumer

    private val userCategoryUpdateDetails = FakeCategoriesData.userCategoryUpdateDetails
    private val userCategory = FakeCategoriesData.userCategories[0]
    private val updatedUserCategory = userCategory.copy(interest = userCategory.interest + CATEGORY_INTEREST_STEP)
    private val newUserCategory = UserCategory(userId = userCategory.userId, categoryId = userCategory.categoryId)
    private val newAddedUserCategory = newUserCategory.copy(id = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun updateUserCategory_existUserCategory() {
        every {
            userCategoriesService.getUserCategory(
                userCategoryUpdateDetails.userId,
                userCategoryUpdateDetails.categoryId
            )
        } returns userCategory

        every { userCategoriesService.addUserCategory(updatedUserCategory) } returns updatedUserCategory

        every {
            kafkaProducer.updateUserDefiningTheme(
                userCategoryUpdateDetails.toUserDefiningThemeUpdateDetails()
            )
        } just Runs

        categoriesKafkaConsumer.updateUserCategory(userCategoryUpdateDetails)
    }

    @Test
    fun updateUserCategory_notExistUserCategory() {
        every {
            userCategoriesService.getUserCategory(
                userCategoryUpdateDetails.userId,
                userCategoryUpdateDetails.categoryId
            )
        } returns null

        every { userCategoriesService.addUserCategory(newUserCategory) } returns newAddedUserCategory

        every {
            kafkaProducer.updateUserDefiningTheme(
                userCategoryUpdateDetails.toUserDefiningThemeUpdateDetails()
            )
        } just Runs

        categoriesKafkaConsumer.updateUserCategory(userCategoryUpdateDetails)
    }
}