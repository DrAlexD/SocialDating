package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
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
            categoryId = 3,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 6,
            name = "RemoteDefiningTheme6",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 7,
            name = "RemoteDefiningTheme7",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 4,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 8,
            name = "RemoteDefiningTheme8",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 4,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 9,
            name = "RemoteDefiningTheme9",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 5,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 10,
            name = "RemoteDefiningTheme10",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 5,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 11,
            name = "RemoteDefiningTheme11",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 6,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 12,
            name = "RemoteDefiningTheme12",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 6,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 13,
            name = "RemoteDefiningTheme13",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 7,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 14,
            name = "RemoteDefiningTheme14",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 7,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 15,
            name = "RemoteDefiningTheme15",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 8,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 16,
            name = "RemoteDefiningTheme16",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 8,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 17,
            name = "RemoteDefiningTheme17",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 9,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 18,
            name = "RemoteDefiningTheme18",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 9,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 19,
            name = "RemoteDefiningTheme19",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 10,
            numberInCategory = 1
        )
    )

    val userDefiningTheme = UserDefiningTheme(id = 1, value = 75, interest = 75, userId = 1, definingThemeId = 1)

    val userDefiningThemeUpdateDetails =
        UserDefiningThemeUpdateDetails(
            userId = 1,
            definingThemeId = 1,
            reactionType = FULL_MAINTAIN,
            isSupportDefiningTheme = true
        )

    fun List<DefiningTheme>.withNullIds() =
        this.map {
            DefiningTheme(
                name = it.name,
                fromOpinion = it.fromOpinion,
                toOpinion = it.toOpinion,
                categoryId = it.categoryId,
                numberInCategory = it.numberInCategory
            )
        }

    fun List<DefiningTheme>.filterByCategoryId(categoryId: Int) = this.filter { it.categoryId == categoryId }

}