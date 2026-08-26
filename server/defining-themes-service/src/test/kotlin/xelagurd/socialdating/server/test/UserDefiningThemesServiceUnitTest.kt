package xelagurd.socialdating.server.test

import kotlin.random.Random
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository
import xelagurd.socialdating.server.service.UserDefiningThemesService

@ExtendWith(MockKExtension::class)
class UserDefiningThemesServiceUnitTest {

    @MockK
    private lateinit var userDefiningThemesRepository: UserDefiningThemesRepository

    @InjectMockKs
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeId = Random.nextInt(1, Int.MAX_VALUE)
    private val userDefiningThemes = FakeDefiningThemesData.userDefiningThemes
    private val userDefiningTheme = FakeDefiningThemesData.userDefiningThemes[0]

    @Test
    fun getUserDefiningThemes_existData_returnsRepositoryResult() {
        every { userDefiningThemesRepository.findAllByUserId(any()) } returns userDefiningThemes

        val result = userDefiningThemesService.getUserDefiningThemes(userId)

        assertEquals(userDefiningThemes, result)

        verify(exactly = 1) { userDefiningThemesRepository.findAllByUserId(userId) }
        confirmVerified(userDefiningThemesRepository)
    }

    @Test
    fun addUserDefiningTheme_validData_savesUserDefiningTheme() {
        every { userDefiningThemesRepository.save(any()) } returns mockk()

        userDefiningThemesService.addUserDefiningTheme(userDefiningTheme)

        verify(exactly = 1) { userDefiningThemesRepository.save(userDefiningTheme) }
        confirmVerified(userDefiningThemesRepository)
    }

    @Test
    fun getUserDefiningTheme_existData_returnsRepositoryResult() {
        every {
            userDefiningThemesRepository.findByUserIdAndDefiningThemeId(any(), any())
        } returns userDefiningTheme

        val result = userDefiningThemesService.getUserDefiningTheme(userId, definingThemeId)

        assertEquals(userDefiningTheme, result)

        verify(exactly = 1) {
            userDefiningThemesRepository.findByUserIdAndDefiningThemeId(userId, definingThemeId)
        }
        confirmVerified(userDefiningThemesRepository)
    }
}
