package xelagurd.socialdating.server.test

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.MaintainedListUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.service.DefiningThemesKafkaConsumer
import xelagurd.socialdating.server.service.DefiningThemesKafkaProducer
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.service.UserDefiningThemesService

@ExtendWith(MockKExtension::class)
class DefiningThemesKafkaConsumerUnitTest {

    @MockK
    private lateinit var definingThemesService: DefiningThemesService

    @MockK
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    @MockK
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    private val updateDetails = FakeDefiningThemesData.userDefiningThemeUpdateDetails
    private val definingTheme = FakeDefiningThemesData.definingThemes[0]
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

    @Test
    fun updateUserDefiningTheme_existData() {
        every {
            userDefiningThemesService.getUserDefiningTheme(updateDetails.userId, updateDetails.definingThemeId)
        } returns userDefiningTheme

        every { userDefiningThemesService.addUserDefiningTheme(updatedUserDefiningTheme) } returns updatedUserDefiningTheme

        every {
            definingThemesService.getDefiningTheme(updateDetails.definingThemeId)
        } returns definingTheme

        every {
            definingThemesKafkaProducer.updateMaintainedList(
                MaintainedListUpdateDetails(
                    userId = userDefiningTheme.userId,
                    categoryId = definingTheme.categoryId,
                    updateType = INCREASE_MAINTAINED,
                    numberInCategory = definingTheme.numberInCategory
                )
            )
        } just Runs

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails)
    }

    @Test
    fun updateUserDefiningTheme_noData() {
        every {
            userDefiningThemesService.getUserDefiningTheme(updateDetails.userId, updateDetails.definingThemeId)
        } returns null

        every { userDefiningThemesService.addUserDefiningTheme(newUserDefiningTheme) } returns newAddedUserDefiningTheme

        definingThemesKafkaConsumer.updateUserDefiningTheme(updateDetails)
    }

}