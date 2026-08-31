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
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.controller.UsersController
import xelagurd.socialdating.server.service.UsersService
import xelagurd.socialdating.server.utils.TestUtils.mockkList
import xelagurd.socialdating.server.utils.TestUtils.nextIntList
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(UsersController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UsersControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var usersService: UsersService

    private val userId = Random.nextInt(ID_MIN, Int.MAX_VALUE)
    private val userIds = Random.nextIntList()

    @Test
    fun getUser_existData_ok() {
        every { usersService.getUser(any()) } returns mockk(relaxed = true)

        mockMvc.perform(
            get("/users/$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { usersService.getUser(any()) }
        confirmVerified(usersService)
    }

    @Test
    fun getUser_noData_noContent() {
        every { usersService.getUser(any()) } returns null

        mockMvc.perform(
            get("/users/$userId")
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { usersService.getUser(any()) }
        confirmVerified(usersService)
    }

    @Test
    fun getUsers_existData_ok() {
        every { usersService.getUsers(any()) } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/users?userIds=${userIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { usersService.getUsers(userIds) }
        confirmVerified(usersService)
    }

    @Test
    fun getUsers_noData_noContent() {
        every { usersService.getUsers(any()) } returns emptyList()

        mockMvc.perform(
            get("/users?userIds=${userIds.toRequestParams()}")
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { usersService.getUsers(userIds) }
        confirmVerified(usersService)
    }
}
