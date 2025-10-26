package xelagurd.socialdating.server.test

import kotlin.test.assertEquals
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
import xelagurd.socialdating.server.controller.UsersController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.service.UsersService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UsersController::class)
@Import(NoSecurityConfig::class)
class UsersControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var usersService: UsersService

    private val users = listOf(
        User(
            id = 1,
            name = "RemoteUser1",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50,
            role = Role.USER
        ),
        User(
            id = 2,
            name = "RemoteUser2",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50,
            role = Role.USER
        ),
        User(
            id = 3,
            name = "RemoteUser3",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50,
            role = Role.USER
        )
    )

    private val userId = 1

    @Test
    fun getUser_allData_success() {
        val expected = users.filter { it.id == userId }

        assertEquals(expected.size, 1)

        `when`(usersService.getUser(userId)).thenReturn(expected[0])

        mockMvc.perform(
            get("/api/v1/users/$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected[0])))
    }

    @Test
    fun getUser_emptyData_error() {
        val message = "test"
        `when`(usersService.getUser(userId)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/api/v1/users/$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }
}