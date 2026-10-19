package xelagurd.socialdating.server.model

object DefaultDataProperties {
    const val GATEWAY_URL = "http://localhost:8080/api/v1"

    const val ID_MIN = 1

    const val PAGE_SIZE_MIN = 1
    const val PAGE_SIZE_DEFAULT = 30
    const val PAGE_SIZE_MAX = 100

    const val NAME_LENGTH_MIN = 2
    const val NAME_LENGTH_MAX = 20
    const val USERNAME_LENGTH_MIN = 2
    const val USERNAME_LENGTH_MAX = 20
    const val PASSWORD_LENGTH_MIN = 8
    const val PASSWORD_LENGTH_MAX = 20
    const val PASSWORD_HASH_LENGTH = 60
    const val TELEGRAM_LENGTH_MIN = 6 // @ and 5 characters of the nickname
    const val TELEGRAM_LENGTH_MAX = 33 // @ and 32 characters of the nickname
    const val CITY_LENGTH_MIN = 2
    const val CITY_LENGTH_MAX = 50
    const val CATEGORY_NAME_LENGTH_MIN = 2
    const val CATEGORY_NAME_LENGTH_MAX = 50
    const val DEFINING_THEME_NAME_LENGTH_MIN = 2
    const val DEFINING_THEME_NAME_LENGTH_MAX = 50
    const val OPINION_LENGTH_MIN = 2
    const val OPINION_LENGTH_MAX = 50
    const val STATEMENT_TEXT_LENGTH_MIN = 2
    const val STATEMENT_TEXT_LENGTH_MAX = 200

    const val USERNAME_PATTERN = "^[a-zA-Z0-9_]+\$"
    // the nickname starts with a letter and doesn't end with an underscore
    const val TELEGRAM_PATTERN = "^@[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?\$"

    const val PERCENT_MIN = 0
    const val PERCENT_MAX = 100

    const val AGE_MIN = 18
    const val AGE_MAX = 99

    const val CATEGORY_INTEREST_STEP = 5

    const val DEFINING_THEME_INTEREST_STEP = 5

    const val DEFINING_THEME_VALUE_INITIAL = 50
    const val DEFINING_THEME_VALUE_STEP = 5
    const val DEFINING_THEME_VALUE_COEFFICIENT = 2
    const val DEFINING_THEME_VALUE_LOW_BORDER = 20
    const val DEFINING_THEME_VALUE_HIGH_BORDER = 80

    const val USER_ACTIVITY_INITIAL = 50

    const val SIMILAR_CATEGORIES_NUMBER = 2
    const val OPPOSITE_CATEGORIES_NUMBER = 2

    const val DIFFERENCE_NUMBER_MIN = 1
}
