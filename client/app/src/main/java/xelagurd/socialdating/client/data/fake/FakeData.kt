package xelagurd.socialdating.client.data.fake

import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.DefiningThemeReactionDetails
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarCategory
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarDefiningTheme
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.additional.SimilarCategory
import xelagurd.socialdating.client.data.model.additional.StatementWithDefiningThemes
import xelagurd.socialdating.client.data.model.enums.Gender.MALE
import xelagurd.socialdating.client.data.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.client.data.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.client.data.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.client.data.model.enums.Role.ADMIN
import xelagurd.socialdating.client.data.model.enums.Role.USER
import xelagurd.socialdating.client.data.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.client.data.model.enums.SimilarityType.SIMILAR
import xelagurd.socialdating.client.data.model.ui.SimilarUserWithData
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
        Category(id = 1, name = "Category1", orderNumber = 1),
        Category(id = 2, name = "Category2", orderNumber = 2),
        Category(id = 3, name = "Category3", orderNumber = 3),
        Category(id = 4, name = "Category4", orderNumber = 4),
        Category(id = 5, name = "Category5", orderNumber = 5),
        Category(id = 6, name = "Category6", orderNumber = 6),
        Category(id = 7, name = "Category7", orderNumber = 7),
        Category(id = 8, name = "Category8", orderNumber = 8),
        Category(id = 9, name = "Category9", orderNumber = 9),
        Category(id = 10, name = "Category10", orderNumber = 10),
        Category(id = 11, name = "Category11", orderNumber = 11),
        Category(id = 12, name = "Category12", orderNumber = 12),
        Category(id = 13, name = "Category13", orderNumber = 13),
        Category(id = 14, name = "Category14", orderNumber = 14),
        Category(id = 15, name = "Category15", orderNumber = 15)
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

    val similarUserCategories = listOf(
        UserCategory(
            id = 4, interest = 30, userId = 2, categoryId = 1
        ),
        UserCategory(
            id = 5, interest = 45, userId = 2, categoryId = 2
        ),
        UserCategory(
            id = 6, interest = 25, userId = 2, categoryId = 3
        )
    )

    val definingThemes = listOf(
        DefiningTheme(
            id = 1,
            name = "DefiningTheme1",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 1,
            orderNumber = 1
        ),
        DefiningTheme(
            id = 2,
            name = "DefiningTheme2",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 2,
            orderNumber = 2
        ),
        DefiningTheme(
            id = 3,
            name = "DefiningTheme3",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 1,
            orderNumber = 1
        ),
        DefiningTheme(
            id = 4,
            name = "DefiningTheme4",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 2,
            orderNumber = 2
        ),
        DefiningTheme(
            id = 5,
            name = "DefiningTheme5",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 3,
            orderNumber = 3
        ),
        DefiningTheme(
            id = 6,
            name = "DefiningTheme6",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 4,
            orderNumber = 4
        ),
        DefiningTheme(
            id = 7,
            name = "DefiningTheme7",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 1,
            numberInCategory = 5,
            orderNumber = 5
        ),
        DefiningTheme(
            id = 8,
            name = "DefiningTheme8",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 3,
            orderNumber = 3
        ),
        DefiningTheme(
            id = 9,
            name = "DefiningTheme9",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 4,
            orderNumber = 4
        ),
        DefiningTheme(
            id = 10,
            name = "DefiningTheme10",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 2,
            numberInCategory = 5,
            orderNumber = 5
        ),
        DefiningTheme(
            id = 11,
            name = "DefiningTheme11",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 1,
            orderNumber = 1
        ),
        DefiningTheme(
            id = 12,
            name = "DefiningTheme12",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 2,
            orderNumber = 2
        ),
        DefiningTheme(
            id = 13,
            name = "DefiningTheme13",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 3,
            orderNumber = 3
        ),
        DefiningTheme(
            id = 14,
            name = "DefiningTheme14",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 4,
            orderNumber = 4
        ),
        DefiningTheme(
            id = 15,
            name = "DefiningTheme15",
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = 3,
            numberInCategory = 5,
            orderNumber = 5
        )
    )

    val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 85, interest = 25, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 80, interest = 25, userId = 1, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 9, interest = 25, userId = 1, definingThemeId = 8),
        UserDefiningTheme(id = 4, value = 0, interest = 25, userId = 1, definingThemeId = 11)
    )

    val similarUserDefiningThemes = listOf(
        UserDefiningTheme(id = 5, value = 78, interest = 30, userId = 2, definingThemeId = 1),
        UserDefiningTheme(id = 6, value = 92, interest = 30, userId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 7, value = 45, interest = 45, userId = 2, definingThemeId = 3),
        UserDefiningTheme(id = 8, value = 88, interest = 45, userId = 2, definingThemeId = 8),
        UserDefiningTheme(id = 9, value = 15, interest = 25, userId = 2, definingThemeId = 11)
    )

    val statementsWithDefiningThemes = listOf(
        StatementWithDefiningThemes(
            id = 6, text = "Statement6", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        ),
        StatementWithDefiningThemes(
            id = 7, text = "Statement7", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        ),
        StatementWithDefiningThemes(
            id = 8, text = "Statement8", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        ),
        StatementWithDefiningThemes(
            id = 9, text = "Statement9", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        ),
        StatementWithDefiningThemes(
            id = 10, text = "Statement10", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(1, true))
        ),
        StatementWithDefiningThemes(
            id = 16, text = "Statement16", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(2, true))
        ),
        StatementWithDefiningThemes(
            id = 17, text = "Statement17", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(2, true))
        ),
        StatementWithDefiningThemes(
            id = 18, text = "Statement18", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(2, true))
        ),
        StatementWithDefiningThemes(
            id = 19, text = "Statement19", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(2, true))
        ),
        StatementWithDefiningThemes(
            id = 20, text = "Statement20", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(2, true))
        ),
        StatementWithDefiningThemes(
            id = 21, text = "Statement21", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(5, true), DefiningThemeReactionDetails(8, false))
        ),
        StatementWithDefiningThemes(
            id = 22, text = "Statement22", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(5, true))
        ),
        StatementWithDefiningThemes(
            id = 23, text = "Statement23", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(5, true))
        ),
        StatementWithDefiningThemes(
            id = 24, text = "Statement24", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(5, true))
        ),
        StatementWithDefiningThemes(
            id = 25, text = "Statement25", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(5, true))
        ),
        StatementWithDefiningThemes(
            id = 31, text = "Statement31", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(8, true))
        ),
        StatementWithDefiningThemes(
            id = 32, text = "Statement32", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(8, true))
        ),
        StatementWithDefiningThemes(
            id = 33, text = "Statement33", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(8, true))
        ),
        StatementWithDefiningThemes(
            id = 34, text = "Statement34", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(8, true))
        ),
        StatementWithDefiningThemes(
            id = 35, text = "Statement35", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(8, true))
        ),
        StatementWithDefiningThemes(
            id = 36, text = "Statement36", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 37, text = "Statement37", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 38, text = "Statement38", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 39, text = "Statement39", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 40, text = "Statement40", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 41, text = "Statement41", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 42, text = "Statement42", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 43, text = "Statement43", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 44, text = "Statement44", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 45, text = "Statement45", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(9, true))
        ),
        StatementWithDefiningThemes(
            id = 51, text = "Statement51", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(11, true))
        ),
        StatementWithDefiningThemes(
            id = 52, text = "Statement52", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(11, true))
        ),
        StatementWithDefiningThemes(
            id = 53, text = "Statement53", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(11, true))
        ),
        StatementWithDefiningThemes(
            id = 54, text = "Statement54", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(11, true))
        ),
        StatementWithDefiningThemes(
            id = 55, text = "Statement55", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(11, true))
        ),
        StatementWithDefiningThemes(
            id = 56, text = "Statement56", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 57, text = "Statement57", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 58, text = "Statement58", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 59, text = "Statement59", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 60, text = "Statement60", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 61, text = "Statement61", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 62, text = "Statement62", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 63, text = "Statement63", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 64, text = "Statement64", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        ),
        StatementWithDefiningThemes(
            id = 65, text = "Statement65", creatorUserId = 1,
            definingThemes = listOf(DefiningThemeReactionDetails(12, true))
        )
    )

    val statements = statementsWithDefiningThemes.map { it.toStatement() }

    val statementDefiningThemes = statementsWithDefiningThemes.flatMap { it.toStatementDefiningThemes() }

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
        definingThemes = mapOf(mainDefiningTheme.id to true, definingThemes[1].id to false),
        creatorUserId = mainStatement.creatorUserId
    )

    val newStatement = StatementWithDefiningThemes(
        id = 66, text = "NewStatement66", creatorUserId = 1,
        definingThemes = listOf(DefiningThemeReactionDetails(1, true))
    )

    private val similarUser = users[1]

    val similarUsers = listOf(
        SimilarUserWithData(
            id = similarUser.id,
            name = similarUser.name,
            gender = similarUser.gender,
            age = similarUser.age,
            city = similarUser.city,
            purpose = similarUser.purpose,
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
