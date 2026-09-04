package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.controller.UserCategoriesController
import xelagurd.socialdating.server.model.dto.DetailedSimilarUserDto
import xelagurd.socialdating.server.model.dto.SimilarUserDto
import xelagurd.socialdating.server.model.enums.Gender.MALE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.server.service.UserCategoriesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList

@WebMvcTest(UserCategoriesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UserCategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val anotherUserId = Random.nextInt(1, Int.MAX_VALUE)

    @Test
    fun getUserCategories_existData_ok() {
        every { userCategoriesService.getUserCategories(any()) } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/categories/users?userId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { userCategoriesService.getUserCategories(any()) }
        confirmVerified(userCategoriesService)
    }

    @Test
    fun getSimilarUsers_existData_ok() {
        every { userCategoriesService.getSimilarUsers(any(), any()) } returns listOf(
            SimilarUserDto(
                id = anotherUserId,
                name = "User",
                gender = MALE,
                age = 27,
                city = "City",
                purpose = FRIENDS,
                similarNumber = 3,
                oppositeNumber = 1,
                similarCategories = emptyList(),
                oppositeCategories = emptyList()
            )
        )

        mockMvc.perform(
            get("/categories/users/similar-users?currentUserId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { userCategoriesService.getSimilarUsers(userId, null) }
        confirmVerified(userCategoriesService)
    }

    @Test
    fun getSimilarUsers_anotherUser_forbidden() {
        every { userCategoriesService.getSimilarUsers(any(), any()) } throws
                AccessDeniedException("Access denied due to request another user`s data")

        mockMvc.perform(
            get("/categories/users/similar-users?currentUserId=$userId")
        )
            .andExpect(status().isForbidden)

        verify(exactly = 1) { userCategoriesService.getSimilarUsers(userId, null) }
        confirmVerified(userCategoriesService)
    }

    @Test
    fun getDetailedSimilarUser_existData_ok() {
        every { userCategoriesService.getDetailedSimilarUser(any(), any()) } returns
                DetailedSimilarUserDto(similarNumber = 5, oppositeNumber = 2, categories = emptyMap())

        mockMvc.perform(
            get("/categories/users/detailed-similar-user?currentUserId=$userId&anotherUserId=$anotherUserId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { userCategoriesService.getDetailedSimilarUser(userId, anotherUserId) }
        confirmVerified(userCategoriesService)
    }
}
