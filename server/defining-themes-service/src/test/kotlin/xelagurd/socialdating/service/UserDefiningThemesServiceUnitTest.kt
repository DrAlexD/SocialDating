package xelagurd.socialdating.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.dto.UserDefiningTheme
import xelagurd.socialdating.dto.UserDefiningThemeDetails
import xelagurd.socialdating.repository.UserDefiningThemesRepository

class UserDefiningThemesServiceUnitTest {

    @MockK
    private lateinit var userDefiningThemesRepository: UserDefiningThemesRepository

    @InjectMockKs
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userCategoryIds = listOf(1, 3)

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )
    private val userDefiningThemeDetails =
        UserDefiningThemeDetails(value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserDefiningThemesByUserCategoryIds() {
        val expected = userDefiningThemes.filter { it.userCategoryId in userCategoryIds }
        every { userDefiningThemesRepository.findAllByUserCategoryIdIn(userCategoryIds) } returns expected

        val result = userDefiningThemesService.getUserDefiningThemes(userCategoryIds)

        assertEquals(expected, result)
    }

    @Test
    fun addUserDefiningTheme() {
        val expected = userDefiningThemes[0]
        every { userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme()) } returns expected

        val result = userDefiningThemesService.addUserDefiningTheme(userDefiningThemeDetails)

        assertEquals(expected, result)
    }
}