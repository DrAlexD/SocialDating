package xelagurd.socialdating.server.utils

import java.time.LocalDate
import java.time.Period

object DateUtils {

    fun LocalDate.toAge(today: LocalDate = LocalDate.now()) = Period.between(this, today).years

    // the latest birth date of the people who are at least of this age
    fun Int.toLatestBirthDate(today: LocalDate = LocalDate.now()): LocalDate = today.minusYears(toLong())

    // the earliest birth date of the people who are at most of this age, they are not of the next age yet
    fun Int.toEarliestBirthDate(today: LocalDate = LocalDate.now()): LocalDate =
        today.minusYears(this + 1L).plusDays(1)
}
