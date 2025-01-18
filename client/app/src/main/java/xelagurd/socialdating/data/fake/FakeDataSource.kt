package xelagurd.socialdating.data.fake

import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose

// FixMe: remove after implementing server
object FakeDataSource {
    val categories = listOf(
        Category(1, "Category1"),
        Category(2, "Category2"),
        Category(3, "Category3"),
        Category(4, "Category4"),
        Category(5, "Category5"),
        Category(6, "Category6"),
        Category(7, "Category7"),
        Category(8, "Category8"),
        Category(9, "Category9"),
        Category(10, "Category10"),
        Category(11, "Category11"),
        Category(12, "Category12"),
        Category(13, "Category13"),
        Category(14, "Category14"),
        Category(15, "Category15"),
        Category(16, "Category16"),
        Category(17, "Category17"),
        Category(18, "Category18"),
        Category(19, "Category19"),
        Category(20, "Category20"),
    )

    val definingThemes = listOf(
        DefiningTheme(1, "DefiningTheme1", "No", "Yes", 1),
        DefiningTheme(2, "DefiningTheme2", "No", "Yes", 1),
        DefiningTheme(3, "DefiningTheme3", "No", "Yes", 1),
        DefiningTheme(4, "DefiningTheme4", "No", "Yes", 2),
        DefiningTheme(5, "DefiningTheme5", "No", "Yes", 3),
        DefiningTheme(6, "DefiningTheme6", "No", "Yes", 5),
        DefiningTheme(7, "DefiningTheme7", "No", "Yes", 1),
        DefiningTheme(8, "DefiningTheme8", "No", "Yes", 2),
        DefiningTheme(9, "DefiningTheme9", "No", "Yes", 3),
        DefiningTheme(10, "DefiningTheme10", "No", "Yes", 4),
        DefiningTheme(11, "DefiningTheme11", "No", "Yes", 1),
        DefiningTheme(12, "DefiningTheme12", "No", "Yes", 2),
        DefiningTheme(13, "DefiningTheme13", "No", "Yes", 6),
        DefiningTheme(14, "DefiningTheme14", "No", "Yes", 7),
        DefiningTheme(15, "DefiningTheme15", "No", "Yes", 2),
        DefiningTheme(16, "DefiningTheme16", "No", "Yes", 3),
        DefiningTheme(17, "DefiningTheme17", "No", "Yes", 5),
        DefiningTheme(18, "DefiningTheme18", "No", "Yes", 9),
        DefiningTheme(19, "DefiningTheme19", "No", "Yes", 10),
        DefiningTheme(20, "DefiningTheme20", "No", "Yes", 8),
    )

    val statements = listOf(
        Statement(1, "Statement1", true, 1, 1),
        Statement(2, "Statement2", true, 1, 1),
        Statement(3, "Statement3", true, 1, 1),
        Statement(4, "Statement4", true, 1, 1),
        Statement(5, "Statement5", true, 1, 1),
        Statement(6, "Statement6", true, 1, 1),
        Statement(7, "Statement7", true, 1, 1),
        Statement(8, "Statement8", true, 2, 1),
        Statement(9, "Statement9", true, 2, 1),
        Statement(10, "Statement10", true, 2, 1),
        Statement(11, "Statement11", true, 2, 1),
        Statement(12, "Statement12", true, 3, 1),
        Statement(13, "Statement13", true, 3, 1),
        Statement(14, "Statement14", true, 3, 1),
        Statement(15, "Statement15", true, 3, 1),
        Statement(16, "Statement16", true, 4, 1),
        Statement(17, "Statement17", true, 4, 1),
        Statement(18, "Statement18", true, 5, 1),
        Statement(19, "Statement19", true, 5, 1),
        Statement(20, "Statement20", true, 5, 1),
    )

    val newStatement = Statement(21, "NewStatement21", true, 1, 1)

    val users = listOf(
        User(
            1, "Alexander", Gender.MALE, "username1", "password1", "email1@gmail.com",
            25, "Moscow", Purpose.ALL_AT_ONCE, 50
        ),
        User(
            2, "Denis", Gender.MALE, "username2", "password2", "email2@gmail.com", 27,
            "St. Petersburg", Purpose.FRIENDS, 50
        ),
        User(
            3, "Andrey", Gender.MALE, "username3", "password3", "email3@gmail.com", 28,
            "Moscow", Purpose.RELATIONSHIPS, 50
        ),
    )

    val userCategories = listOf(
        UserCategory(1, 10, 1, 1),
        UserCategory(2, 5, 1, 2),
        UserCategory(3, 30, 1, 3),
    )

    val userDefiningThemes = listOf(
        UserDefiningTheme(1, 17, 10, 1, 1),
        UserDefiningTheme(2, 89, 5, 1, 2),
        UserDefiningTheme(3, 12, 30, 1, 3),
        UserDefiningTheme(4, 57, 15, 2, 4),
        UserDefiningTheme(5, 15, 50, 3, 5),
    )
}
