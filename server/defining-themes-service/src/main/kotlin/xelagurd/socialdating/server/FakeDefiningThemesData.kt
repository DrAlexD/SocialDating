package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.model.enums.AppLanguage.ENGLISH
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN

object FakeDefiningThemesData {
    val definingThemesDetails = listOf(
        DefiningThemeDetails(
            nameEn = "RemoteDefiningTheme1",
            nameRu = "УдалённаяОпределяющаяТема1",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1
        ),
        DefiningThemeDetails(
            nameEn = "RemoteDefiningTheme2",
            nameRu = "УдалённаяОпределяющаяТема2",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1
        ),
        DefiningThemeDetails(
            nameEn = "RemoteDefiningTheme3",
            nameRu = "УдалённаяОпределяющаяТема3",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2
        ),
        DefiningThemeDetails(
            nameEn = "RemoteDefiningTheme4",
            nameRu = "УдалённаяОпределяющаяТема4",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2
        ),
        DefiningThemeDetails(
            nameEn = "RemoteDefiningTheme5",
            nameRu = "УдалённаяОпределяющаяТема5",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1
        )
    )

    val definingThemes = listOf(
        DefiningTheme(
            id = 1,
            nameEn = "RemoteDefiningTheme1",
            nameRu = "УдалённаяОпределяющаяТема1",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 2,
            nameEn = "RemoteDefiningTheme2",
            nameRu = "УдалённаяОпределяющаяТема2",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 3,
            nameEn = "RemoteDefiningTheme3",
            nameRu = "УдалённаяОпределяющаяТема3",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 4,
            nameEn = "RemoteDefiningTheme4",
            nameRu = "УдалённаяОпределяющаяТема4",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 5,
            nameEn = "RemoteDefiningTheme5",
            nameRu = "УдалённаяОпределяющаяТема5",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 6,
            nameEn = "RemoteDefiningTheme6",
            nameRu = "УдалённаяОпределяющаяТема6",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 7,
            nameEn = "RemoteDefiningTheme7",
            nameRu = "УдалённаяОпределяющаяТема7",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 1,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 8,
            nameEn = "RemoteDefiningTheme8",
            nameRu = "УдалённаяОпределяющаяТема8",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 9,
            nameEn = "RemoteDefiningTheme9",
            nameRu = "УдалённаяОпределяющаяТема9",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 10,
            nameEn = "RemoteDefiningTheme10",
            nameRu = "УдалённаяОпределяющаяТема10",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 2,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 11,
            nameEn = "RemoteDefiningTheme11",
            nameRu = "УдалённаяОпределяющаяТема11",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 3,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 12,
            nameEn = "RemoteDefiningTheme12",
            nameRu = "УдалённаяОпределяющаяТема12",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 3,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 13,
            nameEn = "RemoteDefiningTheme13",
            nameRu = "УдалённаяОпределяющаяТема13",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 3,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 14,
            nameEn = "RemoteDefiningTheme14",
            nameRu = "УдалённаяОпределяющаяТема14",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 3,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 15,
            nameEn = "RemoteDefiningTheme15",
            nameRu = "УдалённаяОпределяющаяТема15",
            fromOpinionEn = "No",
            fromOpinionRu = "Нет",
            toOpinionEn = "Yes",
            toOpinionRu = "Да",
            categoryId = 3,
            numberInCategory = 5
        )
    )

    val definingThemeResponses = definingThemes.map { it.toDefiningThemeResponse(ENGLISH) }

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

    val userDefiningThemesUpdateDetails =
        UserDefiningThemesUpdateDetails(
            userId = 1,
            reactionType = FULL_NO_MAINTAIN,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        )

    fun List<DefiningTheme>.toDefiningThemesWithNullIds() =
        this.map {
            DefiningTheme(
                nameEn = it.nameEn,
                nameRu = it.nameRu,
                fromOpinionEn = it.fromOpinionEn,
                fromOpinionRu = it.fromOpinionRu,
                toOpinionEn = it.toOpinionEn,
                toOpinionRu = it.toOpinionRu,
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

    fun List<DefiningTheme>.filterByIds(definingThemeIds: List<Int>) =
        this.filter { it.id in definingThemeIds }

    fun List<UserDefiningTheme>.filterByUserId(userId: Int) =
        this.filter { it.userId == userId }

}