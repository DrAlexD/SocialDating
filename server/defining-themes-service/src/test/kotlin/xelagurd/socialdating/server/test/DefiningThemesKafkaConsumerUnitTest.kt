package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.service.DefiningThemesKafkaConsumer
import xelagurd.socialdating.server.service.UserDefiningThemesService

class DefiningThemesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    private val userDefiningThemeUpdateDetails = FakeDefiningThemesData.userDefiningThemeUpdateDetails
    private val userDefiningTheme = FakeDefiningThemesData.userDefiningThemes[0]
    private val updatedUserDefiningTheme = userDefiningTheme.copy(
        value = userDefiningTheme.value + DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT,
        interest = userDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP
    )
    private val newUserDefiningTheme = UserDefiningTheme(
        value = DEFINING_THEME_VALUE_INITIAL + DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT,
        userId = userDefiningTheme.userId,
        definingThemeId = userDefiningTheme.definingThemeId
    )
    private val newAddedUserDefiningTheme = newUserDefiningTheme.copy(id = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun updateUserDefiningTheme_existUserDefiningTheme() {
        every {
            userDefiningThemesService.getUserDefiningTheme(
                userDefiningThemeUpdateDetails.userId,
                userDefiningThemeUpdateDetails.definingThemeId
            )
        } returns userDefiningTheme

        every { userDefiningThemesService.addUserDefiningTheme(updatedUserDefiningTheme) } returns updatedUserDefiningTheme

        definingThemesKafkaConsumer.updateUserDefiningTheme(userDefiningThemeUpdateDetails)
    }

    @Test
    fun updateUserDefiningTheme_notExistUserDefiningTheme() {
        every {
            userDefiningThemesService.getUserDefiningTheme(
                userDefiningThemeUpdateDetails.userId,
                userDefiningThemeUpdateDetails.definingThemeId
            )
        } returns null

        every { userDefiningThemesService.addUserDefiningTheme(newUserDefiningTheme) } returns newAddedUserDefiningTheme

        definingThemesKafkaConsumer.updateUserDefiningTheme(userDefiningThemeUpdateDetails)
    }
}