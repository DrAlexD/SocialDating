package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.common.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN

object FakeDefiningThemesData {
    val definingThemesDetails = listOf(
        DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningThemeDetails(name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningThemeDetails(name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningThemeDetails(name = "RemoteDefiningTheme4", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningThemeDetails(name = "RemoteDefiningTheme5", fromOpinion = "No", toOpinion = "Yes", categoryId = 1)
    )

    val definingThemes = listOf(
        DefiningTheme(
            id = 1,
            name = "RemoteDefiningTheme1",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 2,
            name = "RemoteDefiningTheme2",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 3,
            name = "RemoteDefiningTheme3",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 4,
            name = "RemoteDefiningTheme4",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 5,
            name = "RemoteDefiningTheme5",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 6,
            name = "RemoteDefiningTheme6",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 7,
            name = "RemoteDefiningTheme7",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 8,
            name = "RemoteDefiningTheme8",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 9,
            name = "RemoteDefiningTheme9",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 10,
            name = "RemoteDefiningTheme10",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 11,
            name = "RemoteDefiningTheme11",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 12,
            name = "RemoteDefiningTheme12",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 13,
            name = "RemoteDefiningTheme13",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 14,
            name = "RemoteDefiningTheme14",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 15,
            name = "RemoteDefiningTheme15",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 5
        )
    )

    val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 85, interest = 25, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 80, interest = 25, userId = 1, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 9, interest = 25, userId = 1, definingThemeId = 8),
        UserDefiningTheme(id = 4, value = 0, interest = 25, userId = 1, definingThemeId = 11),
        UserDefiningTheme(id = 5, value = 85, interest = 25, userId = 2, definingThemeId = 1),
        UserDefiningTheme(id = 6, value = 80, interest = 25, userId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 7, value = 89, interest = 25, userId = 2, definingThemeId = 8),
        UserDefiningTheme(id = 8, value = 0, interest = 25, userId = 2, definingThemeId = 11),
        UserDefiningTheme(id = 9, value = 90, interest = 25, userId = 3, definingThemeId = 1),
        UserDefiningTheme(id = 10, value = 15, interest = 25, userId = 3, definingThemeId = 2),
        UserDefiningTheme(id = 11, value = 15, interest = 25, userId = 3, definingThemeId = 8),
        UserDefiningTheme(id = 12, value = 95, interest = 25, userId = 3, definingThemeId = 11)
    )

    val userDefiningThemeUpdateDetails =
        UserDefiningThemeUpdateDetails(
            userId = 1,
            definingThemeId = 1,
            reactionType = FULL_NO_MAINTAIN,
            isSupportDefiningTheme = true
        )

    fun List<DefiningTheme>.toDefiningThemesWithNullIds() =
        this.map {
            DefiningTheme(
                name = it.name,
                fromOpinion = it.fromOpinion,
                toOpinion = it.toOpinion,
                categoryId = it.categoryId,
                numberInCategory = it.numberInCategory
            )
        }

    fun List<UserDefiningTheme>.toUserDefiningThemesWithNullIds() =
        this.map {
            UserDefiningTheme(
                value = it.value,
                interest = it.interest,
                userId = it.userId,
                definingThemeId = it.definingThemeId
            )
        }

    fun List<DefiningTheme>.filterByCategoryId(categoryId: Int) =
        this.filter { it.categoryId == categoryId }

}