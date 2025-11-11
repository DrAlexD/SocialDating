package xelagurd.socialdating.server.test

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.service.DefiningThemesService

@ExtendWith(MockKExtension::class)
class DefiningThemesServiceUnitTest {

    @MockK
    private lateinit var definingThemesRepository: DefiningThemesRepository

    @InjectMockKs
    private lateinit var definingThemesService: DefiningThemesService

    private val categoryId = 1

    private val definingThemes = FakeDefiningThemesData.definingThemes

    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingTheme = definingThemes[0]
    private val numberInCategory = definingTheme.numberInCategory

    @Test
    fun getDefiningThemes() {
        every { definingThemesRepository.findAll() } returns definingThemes

        val result = definingThemesService.getDefiningThemes()

        assertEquals(definingThemes, result)
    }

    @Test
    fun getDefiningThemes_categoryId() {
        every { definingThemesRepository.findAllByCategoryId(categoryId) } returns definingThemes

        val result = definingThemesService.getDefiningThemes(categoryId)

        assertEquals(definingThemes, result)
    }

    @Test
    fun addDefiningTheme() {
        every {
            definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId)
        } returns numberInCategory - 1
        every {
            definingThemesRepository.save(definingThemeDetails.toDefiningTheme(numberInCategory))
        } returns definingTheme

        val result = definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(definingTheme, result)
    }

}