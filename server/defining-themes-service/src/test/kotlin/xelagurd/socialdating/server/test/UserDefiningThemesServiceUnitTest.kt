package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository
import xelagurd.socialdating.server.service.UserDefiningThemesService

class UserDefiningThemesServiceUnitTest {

    @MockK
    private lateinit var userDefiningThemesRepository: UserDefiningThemesRepository

    @InjectMockKs
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = 1
    private val definingThemeId = 1

    private val userDefiningThemes = FakeDefiningThemesData.userDefiningThemes
    private val userDefiningTheme = userDefiningThemes[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserDefiningTheme() {
        val expected = userDefiningTheme
        every {
            userDefiningThemesRepository.findByUserIdAndDefiningThemeId(userId, definingThemeId)
        } returns expected

        val result = userDefiningThemesService.getUserDefiningTheme(userId, definingThemeId)

        assertEquals(expected, result)
    }

    @Test
    fun getUserDefiningThemes_allData_success() {
        val expected = userDefiningThemes
        every { userDefiningThemesRepository.findAllByUserId(userId) } returns expected

        val result = userDefiningThemesService.getUserDefiningThemes(userId)

        assertEquals(expected, result)
    }

    @Test
    fun getUserDefiningThemes_emptyData_error() {
        every { userDefiningThemesRepository.findAllByUserId(userId) } returns emptyList()

        assertThrows<NoDataFoundException> { userDefiningThemesService.getUserDefiningThemes(userId) }
    }

}