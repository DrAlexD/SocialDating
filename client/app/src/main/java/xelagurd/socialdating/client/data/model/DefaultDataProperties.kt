package xelagurd.socialdating.client.data.model

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object DefaultDataProperties {

    const val ID_MIN = 1

    const val NAME_LENGTH_MIN = 2
    const val NAME_LENGTH_MAX = 20
    const val USERNAME_LENGTH_MIN = 2
    const val USERNAME_LENGTH_MAX = 20
    const val PASSWORD_LENGTH_MIN = 8
    const val PASSWORD_LENGTH_MAX = 20
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

    const val PERCENT_MIN = 0
    const val PERCENT_MAX = 100

    const val AGE_MIN = 18
    const val AGE_MAX = 99

    // the max border of the age filter means the age without the upper limit,
    // because the users become older than the max age of the registration
    const val FILTER_AGE_MAX = AGE_MAX + 1

    // the date is entered without the separators, as DDMMYYYY
    const val DATE_DIGITS_NUMBER = 8

    const val DIFFERENCE_NUMBER_MIN = 1

    private const val TELEGRAM_PREFIX = "@"
    private const val TELEGRAM_URL = "https://t.me/"

    private val usernameRegex = Regex("^[a-zA-Z0-9_]+\$")
    // the nickname starts with a letter and doesn't end with an underscore
    private val telegramRegex = Regex("^$TELEGRAM_PREFIX[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?\$")

    private val dateDigitsFormatter = DateTimeFormatter.ofPattern("ddMMuuuu").withResolverStyle(ResolverStyle.STRICT)


    fun String.isValidText(minLength: Int, maxLength: Int) = trim().length in minLength..maxLength

    fun String.isValidPassword() = length in PASSWORD_LENGTH_MIN..PASSWORD_LENGTH_MAX

    fun String.isValidUsernameFormat() = usernameRegex.matches(this)

    fun String.isValidUsername() =
        isValidText(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX) && isValidUsernameFormat()

    fun String.isValidTelegramPrefix() = startsWith(TELEGRAM_PREFIX)

    fun String.isValidTelegramFormat() = telegramRegex.matches(this)

    fun String.isValidTelegram() =
        isValidText(TELEGRAM_LENGTH_MIN, TELEGRAM_LENGTH_MAX) && isValidTelegramFormat()

    fun String.toTelegramUrl() = TELEGRAM_URL + removePrefix(TELEGRAM_PREFIX)

    fun String.toDateOrNull(): LocalDate? =
        if (length == DATE_DIGITS_NUMBER) runCatching { LocalDate.parse(this, dateDigitsFormatter) }.getOrNull()
        else null

    fun LocalDate.toAge() = Period.between(this, LocalDate.now()).years

    fun String.isValidDate() = toDateOrNull() != null

    fun String.isValidBirthDate() = toDateOrNull()?.isValidBirthDate() == true

    fun LocalDate.isValidBirthDate() = toAge().isValidAge()

    fun Int.isValidAge() = this in AGE_MIN..AGE_MAX

    fun Int.isValidPercent() = this in PERCENT_MIN..PERCENT_MAX

    fun Int.isValidId() = this >= ID_MIN
}
