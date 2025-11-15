package xelagurd.socialdating.client.data.fake

import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.UserStatement
import xelagurd.socialdating.client.data.model.additional.AuthResponse
import xelagurd.socialdating.client.data.model.enums.Gender.MALE
import xelagurd.socialdating.client.data.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.client.data.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.client.data.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.client.data.model.enums.Role.ADMIN
import xelagurd.socialdating.client.data.model.enums.Role.USER
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData
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
        ),
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
        ),
        UserCategory(
            id = 4, interest = 50, userId = 2, categoryId = 1
        ),
        UserCategory(
            id = 5, interest = 25, userId = 2, categoryId = 2
        ),
        UserCategory(
            id = 6, interest = 25, userId = 2, categoryId = 3
        ),
        UserCategory(
            id = 7, interest = 50, userId = 3, categoryId = 1
        ),
        UserCategory(
            id = 8, interest = 25, userId = 3, categoryId = 2
        ),
        UserCategory(
            id = 9, interest = 25, userId = 3, categoryId = 3
        ),
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
        UserDefiningTheme(id = 4, value = 0, interest = 25, userId = 1, definingThemeId = 11),
        UserDefiningTheme(id = 5, value = 85, interest = 25, userId = 2, definingThemeId = 1),
        UserDefiningTheme(id = 6, value = 80, interest = 25, userId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 7, value = 89, interest = 25, userId = 2, definingThemeId = 8),
        UserDefiningTheme(id = 8, value = 0, interest = 25, userId = 2, definingThemeId = 11),
        UserDefiningTheme(id = 9, value = 90, interest = 25, userId = 3, definingThemeId = 1),
        UserDefiningTheme(id = 10, value = 15, interest = 25, userId = 3, definingThemeId = 2),
        UserDefiningTheme(id = 11, value = 15, interest = 25, userId = 3, definingThemeId = 8),
        UserDefiningTheme(id = 12, value = 95, interest = 25, userId = 3, definingThemeId = 11),
    )

    val statements = listOf(
        Statement(
            id = 1, text = "Statement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 2, text = "Statement2", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 3, text = "Statement3", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 4, text = "Statement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 5, text = "Statement5", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
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
            id = 11, text = "Statement11", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 12, text = "Statement12", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 13, text = "Statement13", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 14, text = "Statement14", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 15, text = "Statement15", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
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
            id = 26, text = "Statement26", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 27, text = "Statement27", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 28, text = "Statement28", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 29, text = "Statement29", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
        ),
        Statement(
            id = 30, text = "Statement30", isSupportDefiningTheme = true, definingThemeId = 8, creatorUserId = 1
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
            id = 46, text = "Statement46", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 47, text = "Statement47", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 48, text = "Statement48", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 49, text = "Statement49", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
        ),
        Statement(
            id = 50, text = "Statement50", isSupportDefiningTheme = true, definingThemeId = 11, creatorUserId = 1
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
        ),
    )

    val userStatements = listOf(
        UserStatement(id = 1, userId = 1, statementId = 1),
        UserStatement(id = 2, userId = 1, statementId = 2),
        UserStatement(id = 3, userId = 1, statementId = 3),
        UserStatement(id = 4, userId = 1, statementId = 4),
        UserStatement(id = 5, userId = 1, statementId = 5),
        UserStatement(id = 6, userId = 1, statementId = 11),
        UserStatement(id = 7, userId = 1, statementId = 12),
        UserStatement(id = 8, userId = 1, statementId = 13),
        UserStatement(id = 9, userId = 1, statementId = 14),
        UserStatement(id = 10, userId = 1, statementId = 15),
        UserStatement(id = 11, userId = 1, statementId = 26),
        UserStatement(id = 12, userId = 1, statementId = 27),
        UserStatement(id = 13, userId = 1, statementId = 28),
        UserStatement(id = 14, userId = 1, statementId = 29),
        UserStatement(id = 15, userId = 1, statementId = 30),
        UserStatement(id = 16, userId = 1, statementId = 46),
        UserStatement(id = 17, userId = 1, statementId = 47),
        UserStatement(id = 18, userId = 1, statementId = 48),
        UserStatement(id = 19, userId = 1, statementId = 49),
        UserStatement(id = 20, userId = 1, statementId = 50),
        UserStatement(id = 21, userId = 2, statementId = 1),
        UserStatement(id = 22, userId = 2, statementId = 2),
        UserStatement(id = 23, userId = 2, statementId = 3),
        UserStatement(id = 24, userId = 2, statementId = 4),
        UserStatement(id = 25, userId = 2, statementId = 5),
        UserStatement(id = 26, userId = 2, statementId = 11),
        UserStatement(id = 27, userId = 2, statementId = 12),
        UserStatement(id = 28, userId = 2, statementId = 13),
        UserStatement(id = 29, userId = 2, statementId = 14),
        UserStatement(id = 30, userId = 2, statementId = 15),
        UserStatement(id = 31, userId = 2, statementId = 26),
        UserStatement(id = 32, userId = 2, statementId = 27),
        UserStatement(id = 33, userId = 2, statementId = 28),
        UserStatement(id = 34, userId = 2, statementId = 29),
        UserStatement(id = 35, userId = 2, statementId = 30),
        UserStatement(id = 36, userId = 2, statementId = 46),
        UserStatement(id = 37, userId = 2, statementId = 47),
        UserStatement(id = 38, userId = 2, statementId = 48),
        UserStatement(id = 39, userId = 2, statementId = 49),
        UserStatement(id = 40, userId = 2, statementId = 50),
        UserStatement(id = 41, userId = 3, statementId = 1),
        UserStatement(id = 42, userId = 3, statementId = 2),
        UserStatement(id = 43, userId = 3, statementId = 5),
        UserStatement(id = 44, userId = 3, statementId = 11),
        UserStatement(id = 45, userId = 3, statementId = 12),
        UserStatement(id = 46, userId = 3, statementId = 3),
        UserStatement(id = 47, userId = 3, statementId = 4),
        UserStatement(id = 48, userId = 3, statementId = 13),
        UserStatement(id = 49, userId = 3, statementId = 14),
        UserStatement(id = 50, userId = 3, statementId = 15),
        UserStatement(id = 51, userId = 3, statementId = 26),
        UserStatement(id = 52, userId = 3, statementId = 27),
        UserStatement(id = 53, userId = 3, statementId = 28),
        UserStatement(id = 54, userId = 3, statementId = 29),
        UserStatement(id = 55, userId = 3, statementId = 30),
        UserStatement(id = 56, userId = 3, statementId = 46),
        UserStatement(id = 57, userId = 3, statementId = 47),
        UserStatement(id = 58, userId = 3, statementId = 48),
        UserStatement(id = 59, userId = 3, statementId = 49),
        UserStatement(id = 60, userId = 3, statementId = 50)
    )

    val loginFormData = LoginFormData(users[0].username, "password1")

    val registrationFormData =
        RegistrationFormData(
            name = users[0].name,
            gender = users[0].gender,
            username = users[0].username,
            password = "password1",
            repeatedPassword = "password1",
            email = "email1@gmail.com",
            age = users[0].age.toString(),
            city = users[0].city,
            purpose = users[0].purpose
        )

    val authResponse = AuthResponse(users[0], "", "")

    val statementFormData = StatementFormData(
        text = statements[0].text,
        isSupportDefiningTheme = statements[0].isSupportDefiningTheme,
        definingThemeId = statements[0].definingThemeId,
        creatorUserId = statements[0].creatorUserId
    )

    val newStatement = Statement(
        id = 66, text = "NewStatement66", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
    )

    val newUserStatement = UserStatement(id = 61, userId = 1, statementId = 6)

    val userCategoryWithData = UserCategoryWithData(
        id = userCategories[0].id,
        interest = userCategories[0].interest,
        userId = userCategories[0].userId,
        categoryId = userCategories[0].categoryId,
        categoryName = categories[0].name
    )

    val userDefiningThemeWithData = UserDefiningThemeWithData(
        id = userDefiningThemes[0].id,
        value = userDefiningThemes[0].value,
        interest = userDefiningThemes[0].interest,
        categoryId = definingThemes[0].categoryId,
        definingThemeId = userDefiningThemes[0].definingThemeId,
        definingThemeName = definingThemes[0].name,
        definingThemeFromOpinion = definingThemes[0].fromOpinion,
        definingThemeToOpinion = definingThemes[0].toOpinion
    )

    val mainUser = users[0]
    val mainCategory = categories[0]
    val mainDefiningTheme = definingThemes[0]
    val mainStatement = statements[5]

    fun List<UserCategory>.filterUserCategoriesByUserId() = this.filter { it.userId == mainUser.id }
    fun List<UserDefiningTheme>.filterUserDefiningThemesByUserId() = this.filter { it.userId == mainUser.id }
    fun List<UserStatement>.filterUserStatementsByUserId() = this.filter { it.userId == mainUser.id }

    const val FAILURE_TEXT = "Failure Text"
    const val ERROR_TEXT = "Error Text"
    const val TEST_TIMEOUT_MILLIS = 3_000L
}
