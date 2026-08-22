package xelagurd.socialdating.server.test

import org.springframework.security.core.userdetails.UsernameNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.security.AuthSecurityConfig

@ExtendWith(MockKExtension::class)
class AuthSecurityConfigUnitTest {

    @MockK
    private lateinit var usersRepository: UsersRepository

    private val authSecurityConfig by lazy { AuthSecurityConfig(usersRepository) }

    private val user = FakeUsersData.users[0]

    @Test
    fun userDetailsService_existingUser_returnsUser() {
        every { usersRepository.findByUsername(user.username) } returns user

        val result = authSecurityConfig.userDetailsService().loadUserByUsername(user.username)

        assertEquals(user, result)
    }

    @Test
    fun userDetailsService_notExistingUser_throwsUsernameNotFound() {
        every { usersRepository.findByUsername(any()) } returns null

        assertThrows(UsernameNotFoundException::class.java) {
            authSecurityConfig.userDetailsService().loadUserByUsername("unknown")
        }
    }
}
