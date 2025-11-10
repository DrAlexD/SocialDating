package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.controller.UsersController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.service.UsersService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UsersController::class)
@Import(NoSecurityConfig::class)
class UsersControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var usersService: UsersService

    private val userId = 1

    private val users = FakeUsersData.users
    private val user = users[0]

    @Test
    fun getUser_allData_success() {
        val expected = user
        `when`(usersService.getUser(userId)).thenReturn(expected)

        mockMvc.perform(
            get("/users/$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUser_emptyData_error() {
        val message = "test"
        `when`(usersService.getUser(userId)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/users/$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }
}