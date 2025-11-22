package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.controller.UserCategoriesController
import xelagurd.socialdating.server.service.UserCategoriesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList

@WebMvcTest(UserCategoriesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UserCategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = Random.nextInt()

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

    @Disabled
    @Test
    fun getSimilarUsers_existData_ok() {
        // TODO
    }

}