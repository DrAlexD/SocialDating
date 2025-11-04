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
import xelagurd.socialdating.client.data.model.enums.StatementReactionType.FULL_MAINTAIN
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
            password = $$"$2a$10$VlL1TDtoHSflx3dUMswP1eJ24xh5IgRVll5JHp9a24mpsArAZQjnm",
            email = "email1@gmail.com",
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
            password = $$"$2a$10$pNja5Q6J680N2HYc2VC7wOza5WoFO6O8e65D9Qwom7OjZkNE3uLe6",
            email = "email2@gmail.com",
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
            password = $$"$2a$10$wfn84flRD.QlmGV4.EGVeO9fXm8sDl9u8.0Cq0MwIq67lgTswjL.e",
            email = "email3@gmail.com",
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
        Category(id = 15, name = "Category15"),
        Category(id = 16, name = "Category16"),
        Category(id = 17, name = "Category17"),
        Category(id = 18, name = "Category18"),
        Category(id = 19, name = "Category19")
    )

    val userCategories = listOf(
        UserCategory(id = 1, interest = 75, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 75, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 75, userId = 1, categoryId = 3),
        UserCategory(id = 4, interest = 75, userId = 2, categoryId = 2)
    )

    val definingThemes = listOf(
        DefiningTheme(id = 1, name = "DefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "DefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 3, name = "DefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 4, name = "DefiningTheme4", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 5, name = "DefiningTheme5", fromOpinion = "No", toOpinion = "Yes", categoryId = 3),
        DefiningTheme(id = 6, name = "DefiningTheme6", fromOpinion = "No", toOpinion = "Yes", categoryId = 3),
        DefiningTheme(id = 7, name = "DefiningTheme7", fromOpinion = "No", toOpinion = "Yes", categoryId = 4),
        DefiningTheme(id = 8, name = "DefiningTheme8", fromOpinion = "No", toOpinion = "Yes", categoryId = 4),
        DefiningTheme(id = 9, name = "DefiningTheme9", fromOpinion = "No", toOpinion = "Yes", categoryId = 5),
        DefiningTheme(id = 10, name = "DefiningTheme10", fromOpinion = "No", toOpinion = "Yes", categoryId = 5),
        DefiningTheme(id = 11, name = "DefiningTheme11", fromOpinion = "No", toOpinion = "Yes", categoryId = 6),
        DefiningTheme(id = 12, name = "DefiningTheme12", fromOpinion = "No", toOpinion = "Yes", categoryId = 6),
        DefiningTheme(id = 13, name = "DefiningTheme13", fromOpinion = "No", toOpinion = "Yes", categoryId = 7),
        DefiningTheme(id = 14, name = "DefiningTheme14", fromOpinion = "No", toOpinion = "Yes", categoryId = 7),
        DefiningTheme(id = 15, name = "DefiningTheme15", fromOpinion = "No", toOpinion = "Yes", categoryId = 8),
        DefiningTheme(id = 16, name = "DefiningTheme16", fromOpinion = "No", toOpinion = "Yes", categoryId = 8),
        DefiningTheme(id = 17, name = "DefiningTheme17", fromOpinion = "No", toOpinion = "Yes", categoryId = 9),
        DefiningTheme(id = 18, name = "DefiningTheme18", fromOpinion = "No", toOpinion = "Yes", categoryId = 9),
        DefiningTheme(id = 19, name = "DefiningTheme19", fromOpinion = "No", toOpinion = "Yes", categoryId = 10)
    )

    val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 75, interest = 75, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 75, interest = 75, userId = 1, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 75, interest = 75, userId = 1, definingThemeId = 3),
        UserDefiningTheme(id = 4, value = 75, interest = 75, userId = 1, definingThemeId = 5),
        UserDefiningTheme(id = 5, value = 75, interest = 75, userId = 2, definingThemeId = 3)
    )

    val statements = listOf(
        Statement(
            id = 1, text = "Statement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 2, text = "Statement2", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 3, text = "Statement3", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 2
        ),
        Statement(
            id = 4, text = "Statement4", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 5, text = "Statement5", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 2
        ),
        Statement(
            id = 6, text = "Statement6", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 2
        ),
        Statement(
            id = 7, text = "Statement7", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 8, text = "Statement8", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
        ),
        Statement(
            id = 9, text = "Statement9", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 10, text = "Statement10", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 11, text = "Statement11", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 12, text = "Statement12", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 13, text = "Statement13", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = 1
        ),
        Statement(
            id = 14, text = "Statement14", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 15, text = "Statement15", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 16, text = "Statement16", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 17, text = "Statement17", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = 1
        ),
        Statement(
            id = 18, text = "Statement18", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        ),
        Statement(
            id = 19, text = "Statement19", isSupportDefiningTheme = true, definingThemeId = 5, creatorUserId = 1
        )
    )

    val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 4),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 1, statementId = 5),
        UserStatement(id = 4, reactionType = FULL_MAINTAIN, userId = 1, statementId = 6),
        UserStatement(id = 5, reactionType = FULL_MAINTAIN, userId = 2, statementId = 5)
    )

    val loginFormData = LoginFormData(users[0].username, "password1")

    val registrationFormData =
        RegistrationFormData(
            name = users[0].name,
            gender = users[0].gender,
            username = users[0].username,
            password = "password1",
            repeatedPassword = "password1",
            email = users[0].email ?: "",
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
        id = 20, text = "NewStatement20", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = 1
    )

    val newUserStatement = UserStatement(id = 6, reactionType = FULL_MAINTAIN, userId = 1, statementId = 2)

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
    val mainStatement = statements[1]

    fun List<UserCategory>.filterUserCategoriesByUserId() = this.filter { it.userId == mainUser.id }
    fun List<UserDefiningTheme>.filterUserDefiningThemesByUserId() = this.filter { it.userId == mainUser.id }
    fun List<UserStatement>.filterUserStatementsByUserId() = this.filter { it.userId == mainUser.id }

    const val FAILURE_TEXT = "Failure Text"
    const val ERROR_TEXT = "Error Text"
    const val TEST_TIMEOUT_MILLIS = 3_000L
}
