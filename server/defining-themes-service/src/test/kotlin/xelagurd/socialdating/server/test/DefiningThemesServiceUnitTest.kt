package xelagurd.socialdating.server.test

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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.repository.DefiningThemesRepository
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList

@ExtendWith(MockKExtension::class)
class DefiningThemesServiceUnitTest {

    @MockK
    private lateinit var definingThemesRepository: DefiningThemesRepository

    @InjectMockKs
    private lateinit var definingThemesService: DefiningThemesService

    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingThemeSlot = slot<DefiningTheme>()
    private val numberInCategory = Random.nextInt()

    @Test
    fun addDefiningTheme() {
        every { definingThemesRepository.findMaxNumberInCategory(any()) } returns numberInCategory
        every { definingThemesRepository.save(capture(definingThemeSlot)) } returns mockk()

        definingThemesService.addDefiningTheme(definingThemeDetails)

        assertEquals(numberInCategory + 1, definingThemeSlot.captured.numberInCategory)

        verify(exactly = 1) { definingThemesRepository.findMaxNumberInCategory(any()) }
        verify(exactly = 1) { definingThemesRepository.save(any()) }
        confirmVerified(definingThemesRepository)
    }

}