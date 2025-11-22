package xelagurd.socialdating.client.data.fake

import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarCategory
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarDefiningTheme
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.additional.SimilarCategory
import xelagurd.socialdating.client.data.model.additional.SimilarUser
import xelagurd.socialdating.client.data.model.enums.Gender.MALE
import xelagurd.socialdating.client.data.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.client.data.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.client.data.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.client.data.model.enums.Role.ADMIN
import xelagurd.socialdating.client.data.model.enums.Role.USER
import xelagurd.socialdating.client.data.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.client.data.model.enums.SimilarityType.SIMILAR
import xelagurd.socialdating.client.ui.form.LoginFormData
import xelagurd.socialdating.client.ui.form.RegistrationFormData
import xelagurd.socialdating.client.ui.form.StatementFormData

object FakeData {
    val users = listOf(
        User(
            id = 1,
            name = "Alexander",
            gender = MALE,
            username = "username1",
            age = 26,
            city = "Moscow",
            purpose = ALL_AT_ONCE,
            activity = 75,
            role = ADMIN
        ),
        User(
            id = 2,
            name = "Denis",
            gender = MALE,
            username = "username2",
            age = 27,
            city = "Moscow",
            purpose = FRIENDS,
            activity = 75,
            role = USER
        ),
        User(
            id = 3,
            name = "Andrey",
            gender = MALE,
            username = "username3",
            age = 27,
            city = "Moscow",
            purpose = RELATIONSHIPS,
            activity = 75,
            role = USER
        )
    )

    val categories = listOf(
        Category(id = 1, name = "Category1"),
        Category(id = 2, name = "Category2"),
        Category(id = 3, name = "Category3"),
        Category(id = 4, name = "Category4"),
        Category(id = 5, name = "Category5"),
        Category(id = 6, name = "Category6"),
        Category(id = 7, name = "Category7"),
        Category(id = 8, name = "Category8"),
        Category(id = 9, name = "Category9"),
        Category(id = 10, name = "Category10"),
        Category(id = 11, name = "Category11"),
        Category(id = 12, name = "Category12"),
        Category(id = 13, name = "Category13"),
        Category(id = 14, name = "Category14"),
        Category(id = 15, name = "Category15")
    )

    val userCategories = listOf(
        UserCategory(
            id = 1, interest = 50, userId = 1, categoryId = 1
        ),
        UserCategory(
            id = 2, interest = 25, userId = 1, categoryId = 2
        ),
        UserCategory(
            id = 3, interest = 25, userId = 1, categoryId = 3
        )
    )

    val definingThemes = listOf(
        DefiningTheme(
            id = 1,
            name = "DefiningTheme1",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 2,
            name = "DefiningTheme2",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 3,
            name = "DefiningTheme3",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 4,
            name = "DefiningTheme4",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 5,
            name = "DefiningTheme5",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 6,
            name = "DefiningTheme6",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 7,
            name = "DefiningTheme7",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 8,
            name = "DefiningTheme8",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 9,
            name = "DefiningTheme9",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 10,
            name = "DefiningTheme10",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 5
        ),
        DefiningTheme(
            id = 11,
            name = "DefiningTheme11",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 1
        ),
        DefiningTheme(
            id = 12,
            name = "DefiningTheme12",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 2
        ),
        DefiningTheme(
            id = 13,
            name = "DefiningTheme13",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 3
        ),
        DefiningTheme(
            id = 14,
            name = "DefiningTheme14",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 4
        ),
        DefiningTheme(
            id = 15,
            name = "DefiningTheme15",
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
        UserDefiningTheme(id = 4, value = 0, interest = 25, userId = 1, definingThemeId = 11)
    )

    val statements = listOf(
        Statement(
            id = 6, text = "Statement6", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 7, text = "Statement7", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 8, text = "Statement8", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 9, text = "Statement9", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 10, text = "Statement10", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 16, text = "Statement16", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 17, text = "Statement17", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 18, text = "Statement18", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 19, text = "Statement19", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 20, text = "Statement20", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 21, text = "Statement21", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 22, text = "Statement22", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 23, text = "Statement23", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 24, text = "Statement24", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 25, text = "Statement25", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 31, text = "Statement31", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 32, text = "Statement32", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 33, text = "Statement33", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 34, text = "Statement34", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 35, text = "Statement35", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 36, text = "Statement36", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 37, text = "Statement37", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 38, text = "Statement38", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 39, text = "Statement39", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 40, text = "Statement40", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 41, text = "Statement41", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 42, text = "Statement42", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 43, text = "Statement43", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 44, text = "Statement44", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 45, text = "Statement45", isSupportDefiningTheme = true, definingThemeId = 9, creatorUserId = 1
        ),
        Statement(
            id = 51, text = "Statement51", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 52, text = "Statement52", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 53, text = "Statement53", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 54, text = "Statement54", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 55, text = "Statement55", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 56, text = "Statement56", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 57, text = "Statement57", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 58, text = "Statement58", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 59, text = "Statement59", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 60, text = "Statement60", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 61, text = "Statement61", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 62, text = "Statement62", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 63, text = "Statement63", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 64, text = "Statement64", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        ),
        Statement(
            id = 65, text = "Statement65", isSupportDefiningTheme = true, definingThemeId = 12, creatorUserId = 1
        )
    )

    val mainUser = users[0]
    val mainCategory = categories[0]
    val mainDefiningTheme = definingThemes[0]
    val mainStatement = statements[0]

    val loginFormData = LoginFormData(mainUser.username, "password1")

    val registrationFormData =
        RegistrationFormData(
            name = mainUser.name,
            gender = mainUser.gender,
            username = mainUser.username,
            password = "password1",
            repeatedPassword = "password1",
            email = "email1@gmail.com",
            age = mainUser.age.toString(),
            city = mainUser.city,
            purpose = mainUser.purpose
        )

    val statementFormData = StatementFormData(
        text = mainStatement.text,
        isSupportDefiningTheme = mainStatement.isSupportDefiningTheme,
        definingThemeId = mainStatement.definingThemeId,
        creatorUserId = mainStatement.creatorUserId
    )

    val newStatement = Statement(
        id = 66, text = "NewStatement66", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
    )

    val similarUsers = listOf(
        SimilarUser(
            id = 2,
            similarNumber = 3,
            oppositeNumber = 1,
            similarCategories = listOf(
                SimilarCategory(name = "Category1", differenceNumber = 2),
                SimilarCategory(name = "Category3", differenceNumber = 1)
            ),
            oppositeCategories = listOf(
                SimilarCategory(name = "Category2", differenceNumber = -1)
            )
        )
    )

    val detailedSimilarUser = DetailedSimilarUser(
        similarNumber = 3,
        oppositeNumber = 1,
        categories = mapOf(
            1 to DetailedSimilarCategory(
                id = 1,
                similarityType = SIMILAR,
                similarNumber = 2,
                oppositeNumber = 0,
                definingThemes = mapOf(
                    1 to DetailedSimilarDefiningTheme(
                        id = 1,
                        similarityType = SIMILAR
                    ),
                    2 to DetailedSimilarDefiningTheme(
                        id = 2,
                        similarityType = SIMILAR
                    )
                )
            ),
            2 to DetailedSimilarCategory(
                id = 2,
                similarityType = OPPOSITE,
                similarNumber = 0,
                oppositeNumber = 1,
                definingThemes = mapOf(
                    3 to DetailedSimilarDefiningTheme(
                        id = 3,
                        similarityType = OPPOSITE
                    )
                )
            ),
            3 to DetailedSimilarCategory(
                id = 3,
                similarityType = SIMILAR,
                similarNumber = 1,
                oppositeNumber = 0,
                definingThemes = mapOf(
                    1 to DetailedSimilarDefiningTheme(
                        id = 1,
                        similarityType = SIMILAR
                    )
                )
            )
        )
    )

    const val FAILURE_TEXT = "Failure Text"
    const val ERROR_TEXT = "Error Text"
    const val TEST_TIMEOUT_MILLIS = 3_000L
}
