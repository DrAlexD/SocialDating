package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN

object FakeDefiningThemesData {
    val definingThemesDetails = listOf(
        DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningThemeDetails(name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningThemeDetails(name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningThemeDetails(name = "RemoteDefiningTheme4", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningThemeDetails(name = "RemoteDefiningTheme5", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )

    val definingThemes = listOf(
        DefiningTheme(id = 1, name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 3, name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 4, name = "RemoteDefiningTheme4", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 5, name = "RemoteDefiningTheme5", fromOpinion = "No", toOpinion = "Yes", categoryId = 3),
        DefiningTheme(id = 6, name = "RemoteDefiningTheme6", fromOpinion = "No", toOpinion = "Yes", categoryId = 3),
        DefiningTheme(id = 7, name = "RemoteDefiningTheme7", fromOpinion = "No", toOpinion = "Yes", categoryId = 4),
        DefiningTheme(id = 8, name = "RemoteDefiningTheme8", fromOpinion = "No", toOpinion = "Yes", categoryId = 4),
        DefiningTheme(id = 9, name = "RemoteDefiningTheme9", fromOpinion = "No", toOpinion = "Yes", categoryId = 5),
        DefiningTheme(id = 10, name = "RemoteDefiningTheme10", fromOpinion = "No", toOpinion = "Yes", categoryId = 5),
        DefiningTheme(id = 11, name = "RemoteDefiningTheme11", fromOpinion = "No", toOpinion = "Yes", categoryId = 6),
        DefiningTheme(id = 12, name = "RemoteDefiningTheme12", fromOpinion = "No", toOpinion = "Yes", categoryId = 6),
        DefiningTheme(id = 13, name = "RemoteDefiningTheme13", fromOpinion = "No", toOpinion = "Yes", categoryId = 7),
        DefiningTheme(id = 14, name = "RemoteDefiningTheme14", fromOpinion = "No", toOpinion = "Yes", categoryId = 7),
        DefiningTheme(id = 15, name = "RemoteDefiningTheme15", fromOpinion = "No", toOpinion = "Yes", categoryId = 8),
        DefiningTheme(id = 16, name = "RemoteDefiningTheme16", fromOpinion = "No", toOpinion = "Yes", categoryId = 8),
        DefiningTheme(id = 17, name = "RemoteDefiningTheme17", fromOpinion = "No", toOpinion = "Yes", categoryId = 9),
        DefiningTheme(id = 18, name = "RemoteDefiningTheme18", fromOpinion = "No", toOpinion = "Yes", categoryId = 9),
        DefiningTheme(id = 19, name = "RemoteDefiningTheme19", fromOpinion = "No", toOpinion = "Yes", categoryId = 10)
    )

    val userDefiningThemesDetails = listOf(
        UserDefiningThemeDetails(value = 75, interest = 75, userId = 1, definingThemeId = 1),
        UserDefiningThemeDetails(value = 75, interest = 75, userId = 1, definingThemeId = 2),
        UserDefiningThemeDetails(value = 75, interest = 75, userId = 1, definingThemeId = 3),
        UserDefiningThemeDetails(value = 75, interest = 75, userId = 1, definingThemeId = 5),
        UserDefiningThemeDetails(value = 75, interest = 75, userId = 2, definingThemeId = 3)
    )

    val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 75, interest = 75, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 75, interest = 75, userId = 1, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 75, interest = 75, userId = 1, definingThemeId = 3),
        UserDefiningTheme(id = 4, value = 75, interest = 75, userId = 1, definingThemeId = 5),
        UserDefiningTheme(id = 5, value = 75, interest = 75, userId = 2, definingThemeId = 3)
    )

    val userDefiningThemeUpdateDetails =
        UserDefiningThemeUpdateDetails(
            userId = 1,
            definingThemeId = 1,
            reactionType = FULL_MAINTAIN,
            isSupportDefiningTheme = true
        )

    fun List<DefiningTheme>.withNullIds(): List<DefiningTheme> =
        this.map {
            DefiningTheme(
                name = it.name,
                fromOpinion = it.fromOpinion,
                toOpinion = it.toOpinion,
                categoryId = it.categoryId
            )
        }
}