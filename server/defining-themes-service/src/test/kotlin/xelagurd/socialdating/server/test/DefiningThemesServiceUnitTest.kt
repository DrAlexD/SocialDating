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
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.service.DefiningThemesService

class DefiningThemesServiceUnitTest {

    @MockK
    private lateinit var definingThemesRepository: DefiningThemesRepository

    @InjectMockKs
    private lateinit var definingThemesService: DefiningThemesService

    private val categoryId = 1

    private val definingThemes = FakeDefiningThemesData.definingThemes

    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingTheme = definingThemes[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getDefiningThemesByCategoryId() {
        val expected = definingThemes
        every { definingThemesRepository.findAllByCategoryId(categoryId) } returns expected

        val result = definingThemesService.getDefiningThemes(categoryId)

        assertEquals(expected, result)
    }

    @Test
    fun getDefiningThemes_allData_success() {
        val expected = definingThemes
        every { definingThemesRepository.findAll() } returns expected

        val result = definingThemesService.getDefiningThemes()

        assertEquals(expected, result)
    }

    @Test
    fun getDefiningThemes_emptyData_error() {
        every { definingThemesRepository.findAll() } returns emptyList()

        assertThrows<NoDataFoundException> { definingThemesService.getDefiningThemes() }
    }

    @Test
    fun addDefiningTheme() {
        val expected = definingTheme
        every {
            definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId)
        } returns expected.numberInCategory - 1
        every {
            definingThemesRepository.save(definingThemeDetails.toDefiningTheme(expected.numberInCategory))
        } returns expected

        val result = definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(expected, result)
    }
}