package xelagurd.socialdating.server.test

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.DateUtils.toAge
import xelagurd.socialdating.server.utils.DateUtils.toEarliestBirthDate
import xelagurd.socialdating.server.utils.DateUtils.toLatestBirthDate

class DateUtilsUnitTest {

    private val today = LocalDate.of(2026, 10, 18)

    @Test
    fun toAge_birthdayPassed_returnsFullYears() {
        assertEquals(26, LocalDate.of(2000, 3, 15).toAge(today))
    }

    @Test
    fun toAge_birthdayToday_returnsFullYears() {
        assertEquals(26, LocalDate.of(2000, 10, 18).toAge(today))
    }

    @Test
    fun toAge_birthdayTomorrow_returnsPreviousYears() {
        assertEquals(25, LocalDate.of(2000, 10, 19).toAge(today))
    }

    @Test
    fun toAge_leapDayBirthInNotLeapYear_comesOfAgeOnFirstOfMarch() {
        val birthDate = LocalDate.of(2008, 2, 29)

        assertEquals(17, birthDate.toAge(LocalDate.of(2026, 2, 28)))
        assertEquals(18, birthDate.toAge(LocalDate.of(2026, 3, 1)))
    }

    @Test
    fun toAge_futureBirthDate_returnsNegativeAge() {
        assertEquals(-1, today.plusYears(1).toAge(today))
    }

    @Test
    fun toLatestBirthDate_age_returnsBirthDateOfThisBirthdayToday() {
        assertEquals(LocalDate.of(2008, 10, 18), 18.toLatestBirthDate(today))
    }

    @Test
    fun toEarliestBirthDate_age_returnsDayAfterBirthDateOfNextAgeToday() {
        assertEquals(LocalDate.of(2007, 10, 19), 18.toEarliestBirthDate(today))
    }

    // the borders are checked around the leap days, where the years have the different lengths
    @Test
    fun birthDateBorders_anyDates_matchAge() {
        val days = generateSequence(LocalDate.of(2027, 12, 1)) { it.plusDays(1) }
            .takeWhile { it <= LocalDate.of(2029, 3, 31) }
            .toList()
        val age = 18

        days.forEach { day ->
            val latestBirthDate = age.toLatestBirthDate(day)
            val earliestBirthDate = age.toEarliestBirthDate(day)

            generateSequence(day.minusYears(age + 2L)) { it.plusDays(1) }
                .takeWhile { it <= day.minusYears(age - 1L) }
                .forEach { birthDate ->
                    val birthDateAge = birthDate.toAge(day)

                    assertEquals(birthDateAge >= age, birthDate <= latestBirthDate, "$birthDate on $day")
                    assertEquals(birthDateAge <= age, birthDate >= earliestBirthDate, "$birthDate on $day")
                }
        }
    }
}
