package xelagurd.socialdating.server.test

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
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.service.DefiningThemesKafkaConsumer
import xelagurd.socialdating.server.service.DefiningThemesKafkaProducer
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.service.UserDefiningThemesService

@ExtendWith(MockKExtension::class)
class DefiningThemesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    @MockK(relaxed = true)
    private lateinit var definingThemesService: DefiningThemesService

    @MockK(relaxed = true)
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    private val userDefiningTheme = FakeDefiningThemesData.userDefiningTheme
    private val userDefiningThemeSlot = slot<UserDefiningTheme>()
    private val updateDetails = FakeDefiningThemesData.userDefiningThemeUpdateDetails

    @Test
    fun updateUserDefiningTheme_existData_updateMaintained() {
        every {
            userDefiningThemesService.getUserDefiningTheme(any(), any())
        } returns userDefiningTheme

        every {
            userDefiningThemesService.addUserDefiningTheme(capture(userDefiningThemeSlot))
        } returns mockk()

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails)

        assertEquals(
            userDefiningTheme.value + DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT,
            userDefiningThemeSlot.captured.value
        )

        assertEquals(
            userDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP,
            userDefiningThemeSlot.captured.interest
        )

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesService.getDefiningTheme(any()) }
        verify(exactly = 1) { definingThemesKafkaProducer.updateMaintainedList(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_existData_noUpdateMaintained() {
        val initialUserDefiningTheme = userDefiningTheme.copy(value = DEFINING_THEME_VALUE_INITIAL)
        every {
            userDefiningThemesService.getUserDefiningTheme(any(), any())
        } returns initialUserDefiningTheme

        every {
            userDefiningThemesService.addUserDefiningTheme(capture(userDefiningThemeSlot))
        } returns mockk()

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails)

        assertEquals(
            initialUserDefiningTheme.value + DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT,
            userDefiningThemeSlot.captured.value
        )

        assertEquals(
            initialUserDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP,
            userDefiningThemeSlot.captured.interest
        )

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

    @Test
    fun updateUserDefiningTheme_noData() {
        every {
            userDefiningThemesService.getUserDefiningTheme(any(), any())
        } returns null

        every {
            userDefiningThemesService.addUserDefiningTheme(capture(userDefiningThemeSlot))
        } returns mockk()

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails)

        assertEquals(
            DEFINING_THEME_VALUE_INITIAL + DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT,
            userDefiningThemeSlot.captured.value
        )

        assertEquals(
            DEFINING_THEME_INTEREST_STEP,
            userDefiningThemeSlot.captured.interest
        )

        verify(exactly = 1) { userDefiningThemesService.getUserDefiningTheme(any(), any()) }
        verify(exactly = 1) { userDefiningThemesService.addUserDefiningTheme(any()) }
        confirmVerified(userDefiningThemesService, definingThemesService, definingThemesKafkaProducer)
    }

}