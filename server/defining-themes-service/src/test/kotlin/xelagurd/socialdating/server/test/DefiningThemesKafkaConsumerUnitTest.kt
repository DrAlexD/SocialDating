package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.service.DefiningThemesKafkaConsumer
import xelagurd.socialdating.server.service.UserDefiningThemesService

class DefiningThemesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    @InjectMockKs
    private lateinit var definingThemesKafkaConsumer: DefiningThemesKafkaConsumer

    val userId = 1
    val definingThemeId = 1

    private val userDefiningThemeUpdateDetails = UserDefiningThemeUpdateDetails(
        userId = userId,
        definingThemeId = definingThemeId,
        reactionType = StatementReactionType.FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    private val userDefiningTheme =
        UserDefiningTheme(id = 1, value = 50, interest = 10, userId = userId, definingThemeId = definingThemeId)
    private val updatedUserDefiningTheme =
        UserDefiningTheme(id = 1, value = 50, interest = 15, userId = userId, definingThemeId = definingThemeId)
    private val newUserDefiningTheme = UserDefiningTheme(
        value = 60,
        interest = 5,
        userId = userId,
        definingThemeId = definingThemeId
    )
    private val newAddedUserDefiningTheme = UserDefiningTheme(
        value = 60,
        interest = 5,
        userId = userId,
        definingThemeId = definingThemeId
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun consumeStatementReactionWithExistUserDefiningTheme() {
        every {
            userDefiningThemesService.getUserDefiningTheme(
                userDefiningThemeUpdateDetails.userId,
                userDefiningThemeUpdateDetails.definingThemeId
            )
        } returns userDefiningTheme

        every { userDefiningThemesService.addUserDefiningTheme(userDefiningTheme) } returns updatedUserDefiningTheme

        definingThemesKafkaConsumer.consumeStatementReaction(userDefiningThemeUpdateDetails)
    }

    @Test
    fun consumeStatementReactionWithNotExistUserDefiningTheme() {
        every {
            userDefiningThemesService.getUserDefiningTheme(
                userDefiningThemeUpdateDetails.userId,
                userDefiningThemeUpdateDetails.definingThemeId
            )
        } returns null

        every { userDefiningThemesService.addUserDefiningTheme(newUserDefiningTheme) } returns newAddedUserDefiningTheme

        definingThemesKafkaConsumer.consumeStatementReaction(userDefiningThemeUpdateDetails)
    }
}