package xelagurd.socialdating.server.test

import java.util.Optional
import kotlin.random.Random
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.nextIntList

@ExtendWith(MockKExtension::class)
class DefiningThemesServiceUnitTest {

    @MockK
    private lateinit var definingThemesRepository: DefiningThemesRepository

    @InjectMockKs
    private lateinit var definingThemesService: DefiningThemesService

    private val definingThemeId = Random.nextInt(1, Int.MAX_VALUE)
    private val categoryId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = Random.nextIntList()
    private val definingThemes = FakeDefiningThemesData.definingThemes
    private val definingTheme = FakeDefiningThemesData.definingThemes[0]
    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingThemeSlot = slot<DefiningTheme>()
    private val maxNumberInCategory = Random.nextInt(1, Int.MAX_VALUE)

    @Test
    fun getDefiningThemes_existData_returnsRepositoryResult() {
        every { definingThemesRepository.findAllByIdsAndCategoryId(any(), any()) } returns definingThemes

        val result = definingThemesService.getDefiningThemes(definingThemeIds, categoryId)

        assertEquals(definingThemes, result)

        verify(exactly = 1) {
            definingThemesRepository.findAllByIdsAndCategoryId(definingThemeIds, categoryId)
        }
        confirmVerified(definingThemesRepository)
    }

    @Test
    fun getDefiningTheme_existData_returnsDefiningTheme() {
        every { definingThemesRepository.findById(any()) } returns Optional.of(definingTheme)

        val result = definingThemesService.getDefiningTheme(definingThemeId)

        assertEquals(definingTheme, result)

        verify(exactly = 1) { definingThemesRepository.findById(definingThemeId) }
        confirmVerified(definingThemesRepository)
    }

    @Test
    fun getDefiningTheme_noData_returnsNull() {
        every { definingThemesRepository.findById(any()) } returns Optional.empty()

        val result = definingThemesService.getDefiningTheme(definingThemeId)

        assertNull(result)

        verify(exactly = 1) { definingThemesRepository.findById(definingThemeId) }
        confirmVerified(definingThemesRepository)
    }

    @Test
    fun addDefiningTheme_existingMaxNumber_incrementsNumberInCategory() {
        every { definingThemesRepository.findMaxNumberInCategory(any()) } returns maxNumberInCategory
        every { definingThemesRepository.save(capture(definingThemeSlot)) } returns mockk()

        definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(maxNumberInCategory + 1, definingThemeSlot.captured.numberInCategory)
        with(definingThemeSlot.captured) {
            assertEquals(definingThemeDetails.name, name)
            assertEquals(definingThemeDetails.fromOpinion, fromOpinion)
            assertEquals(definingThemeDetails.toOpinion, toOpinion)
            assertEquals(definingThemeDetails.categoryId, categoryId)
        }

        verify(exactly = 1) { definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId) }
        verify(exactly = 1) { definingThemesRepository.save(any()) }
        confirmVerified(definingThemesRepository)
    }

    @Test
    fun addDefiningTheme_noMaxNumber_setsNumberInCategoryToOne() {
        every { definingThemesRepository.findMaxNumberInCategory(any()) } returns null
        every { definingThemesRepository.save(capture(definingThemeSlot)) } returns mockk()

        definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(1, definingThemeSlot.captured.numberInCategory)

        verify(exactly = 1) { definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId) }
        verify(exactly = 1) { definingThemesRepository.save(any()) }
        confirmVerified(definingThemesRepository)
    }
}
