package xelagurd.socialdating.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import xelagurd.socialdating.controller.UsersController
import xelagurd.socialdating.dto.User
import xelagurd.socialdating.service.TestUtils.convertObjectToJsonString
import kotlin.test.assertEquals

@WebMvcTest(UsersController::class)
class UsersControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var usersService: UsersService

    private val users = listOf(
        User(id = 1, name = "RemoteUser1"),
        User(id = 2, name = "RemoteUser2"),
        User(id = 3, name = "RemoteUser3")
    )

    private val userId = 1

    @Test
    fun getUser() {
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
}