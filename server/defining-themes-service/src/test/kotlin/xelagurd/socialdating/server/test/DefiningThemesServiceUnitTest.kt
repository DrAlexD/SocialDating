package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.service.DefiningThemesService

class DefiningThemesServiceUnitTest {

    @MockK
    private lateinit var definingThemesRepository: DefiningThemesRepository

    @InjectMockKs
    private lateinit var definingThemesService: DefiningThemesService

    private val categoryId = 1
    private val categoryIds = listOf(1, 3)

    private val definingThemes = listOf(
        DefiningTheme(id = 1, name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 3, name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )
    private val definingThemeDetails =
        DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getDefiningThemesByCategoryId() {
        val expected = definingThemes.filter { it.categoryId == categoryId }
        every { definingThemesRepository.findAllByCategoryIdIn(listOf(categoryId)) } returns expected

        val result = definingThemesService.getDefiningThemes(listOf(categoryId))

        assertEquals(expected, result)
    }

    @Test
    fun getDefiningThemesByCategoryIds() {
        val expected = definingThemes.filter { it.categoryId in categoryIds }
        every { definingThemesRepository.findAllByCategoryIdIn(categoryIds) } returns expected

        val result = definingThemesService.getDefiningThemes(categoryIds)

        assertEquals(expected, result)
    }

    @Test
    fun addDefiningTheme() {
        val expected = definingThemes[0]
        every { definingThemesRepository.save(definingThemeDetails.toDefiningTheme()) } returns expected

        val result = definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(expected, result)
    }
}