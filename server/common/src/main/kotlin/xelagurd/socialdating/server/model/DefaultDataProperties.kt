package xelagurd.socialdating.server.model

object DefaultDataProperties {
    const val GATEWAY_URL = "http://localhost:8080/api/v1"

    const val ID_MIN = 1

    const val NAME_LENGTH_MIN = 2
    const val NAME_LENGTH_MAX = 20
    const val USERNAME_LENGTH_MIN = 2
    const val USERNAME_LENGTH_MAX = 20
    const val PASSWORD_LENGTH_MIN = 8
    const val PASSWORD_LENGTH_MAX = 20
    const val PASSWORD_HASH_LENGTH = 60
    const val EMAIL_LENGTH_MIN = 5 // a@b.c
    const val EMAIL_LENGTH_MAX = 50
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
    private const val EMAIL_LOCAL_PART = "[A-Za-z0-9_%+-]+(\\.[A-Za-z0-9_%+-]+)*"
    private const val EMAIL_DOMAIN_LABEL = "[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?"
    const val EMAIL_PATTERN = "^$EMAIL_LOCAL_PART@$EMAIL_DOMAIN_LABEL(\\.$EMAIL_DOMAIN_LABEL)+\$"

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
}
