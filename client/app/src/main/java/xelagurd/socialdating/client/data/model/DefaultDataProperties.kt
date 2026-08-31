package xelagurd.socialdating.client.data.model

object DefaultDataProperties {

    const val ID_MIN = 1

    const val NAME_LENGTH_MIN = 2
    const val NAME_LENGTH_MAX = 20
    const val USERNAME_LENGTH_MIN = 2
    const val USERNAME_LENGTH_MAX = 20
    const val PASSWORD_LENGTH_MIN = 8
    const val PASSWORD_LENGTH_MAX = 20
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

    const val PERCENT_MIN = 0
    const val PERCENT_MAX = 100

    const val AGE_MIN = 18
    const val AGE_MAX = 99

    private const val EMAIL_LOCAL_PART = "[A-Za-z0-9_%+-]+(\\.[A-Za-z0-9_%+-]+)*"
    private const val EMAIL_DOMAIN_LABEL = "[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?"

    private val usernameRegex = Regex("^[a-zA-Z0-9_]+\$")
    private val emailRegex = Regex("^$EMAIL_LOCAL_PART@$EMAIL_DOMAIN_LABEL(\\.$EMAIL_DOMAIN_LABEL)+\$")


    fun String.isValidText(minLength: Int, maxLength: Int) = trim().length in minLength..maxLength

    fun String.isValidPassword() = length in PASSWORD_LENGTH_MIN..PASSWORD_LENGTH_MAX

    fun String.isValidUsername() =
        isValidText(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX) && usernameRegex.matches(this)

    fun String.isValidEmail() = isValidText(EMAIL_LENGTH_MIN, EMAIL_LENGTH_MAX) && emailRegex.matches(this)

    fun String.isValidAge() = toIntOrNull()?.isValidAge() == true

    fun Int.isValidAge() = this in AGE_MIN..AGE_MAX

    fun Int.isValidPercent() = this in PERCENT_MIN..PERCENT_MAX

    fun Int.isValidId() = this >= ID_MIN
}
