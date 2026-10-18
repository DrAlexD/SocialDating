package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
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
import xelagurd.socialdating.server.controller.UsersController
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.details.UsersFilterDetails
import xelagurd.socialdating.server.model.enums.Gender.MALE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
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

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
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

    @Test
    fun getUserIds_withFilter_ok() {
        val usersFilterDetails = UsersFilterDetails(
            genders = listOf(MALE),
            ageMin = 20,
            ageMax = 30,
            purposes = listOf(FRIENDS),
            sameCityUserId = userId
        )
        every { usersService.getUserIds(any()) } returns userIds

        mockMvc.perform(
            get("/users/ids?genders=MALE&ageMin=20&ageMax=30&purposes=FRIENDS&sameCityUserId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { usersService.getUserIds(usersFilterDetails) }
        confirmVerified(usersService)
    }

    @Test
    fun getUserIds_noData_noContent() {
        every { usersService.getUserIds(any()) } returns emptyList()

        mockMvc.perform(
            get("/users/ids")
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { usersService.getUserIds(UsersFilterDetails()) }
        confirmVerified(usersService)
    }

    @Test
    fun getUserIds_wrongFilter_badRequest() {
        val wrongFilterQueries = listOf(
            "ageMin=${AGE_MIN - 1}",
            "ageMin=${AGE_MAX + 1}",
            "ageMax=${AGE_MAX + 1}",
            "ageMin=30&ageMax=20",
            "sameCityUserId=0"
        )

        wrongFilterQueries.forEach {
            mockMvc.perform(
                get("/users/ids?$it")
            )
                .andExpect(status().isBadRequest)
        }

        verify(exactly = 0) { usersService.getUserIds(any()) }
        confirmVerified(usersService)
    }
}
