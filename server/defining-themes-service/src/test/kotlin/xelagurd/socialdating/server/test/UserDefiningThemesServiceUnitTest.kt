package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository
import xelagurd.socialdating.server.service.UserDefiningThemesService

class UserDefiningThemesServiceUnitTest {

    @MockK
    private lateinit var userDefiningThemesRepository: UserDefiningThemesRepository

    @InjectMockKs
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = 1
    private val definingThemeId = 1

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userId = 3, definingThemeId = 3)
    )
    private val userDefiningThemeDetails =
        UserDefiningThemeDetails(value = 10, interest = 10, userId = 1, definingThemeId = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserDefiningTheme() {
        val expected = userDefiningThemes
            .filter { it.userId == userId && it.definingThemeId == definingThemeId }

        assertEquals(expected.size, 1)

        every {
            userDefiningThemesRepository.findByUserIdAndDefiningThemeId(
                userId,
                definingThemeId
            )
        } returns expected[0]

        val result = userDefiningThemesService.getUserDefiningTheme(userId, definingThemeId)

        assertEquals(expected[0], result)
    }

    @Test
    fun getUserDefiningThemesByUserCategoryIds_allData_success() {
        val expected = userDefiningThemes.filter { it.userId == userId }
        every { userDefiningThemesRepository.findAllByUserId(userId) } returns expected

        val result = userDefiningThemesService.getUserDefiningThemes(userId)

        assertEquals(expected, result)
    }

    @Test
    fun getUserDefiningThemesByUserCategoryIds_emptyData_error() {
        every { userDefiningThemesRepository.findAllByUserId(userId) } returns emptyList()

        assertThrows<NoDataFoundException> { userDefiningThemesService.getUserDefiningThemes(userId) }
    }

    @Test
    fun addUserDefiningTheme() {
        val expected = userDefiningThemes[0]
        every { userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme()) } returns expected

        val result = userDefiningThemesService.addUserDefiningTheme(userDefiningThemeDetails)

        assertEquals(expected, result)
    }
}