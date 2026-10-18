package xelagurd.socialdating.client.test

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.FILTER_AGE_MAX
import xelagurd.socialdating.client.data.model.details.SimilarUsersFilterDetails
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType.CUSTOM
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType.DEFAULT
import xelagurd.socialdating.client.data.model.enums.Gender.FEMALE
import xelagurd.socialdating.client.data.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.client.ui.form.FormFieldError
import xelagurd.socialdating.client.ui.form.SimilarUsersFilterFormData
import xelagurd.socialdating.client.ui.form.toSimilarUsersFilterFormData

class SimilarUsersFilterFormDataTest {

    private val emptyFormData = SimilarUsersFilterFormData()
    private val formData = FakeData.similarUsersFilterFormData
    private val customRangeFormData = emptyFormData.copy(differenceRangeType = CUSTOM)

    private val integerError = FormFieldError(R.string.error_integer)
    private val emptyRangeError = FormFieldError(R.string.error_empty_range)
    private val lessThanMinError = FormFieldError(R.string.error_less_than_min)

    @Test
    fun similarUsersFilterFormData_emptyData_isValidWithoutActiveFiltersAndErrors() {
        assertEquals(DEFAULT, emptyFormData.differenceRangeType)
        assertTrue(emptyFormData.isValid)
        assertEquals(0, emptyFormData.activeFiltersNumber)
        assertNull(emptyFormData.minDifferenceNumberError)
        assertNull(emptyFormData.maxDifferenceNumberError)
    }

    @Test
    fun similarUsersFilterFormData_allData_isValidWithEveryFilterCountedOnce() {
        assertTrue(formData.isValid)
        assertEquals(6, formData.activeFiltersNumber)
        assertNull(formData.minDifferenceNumberError)
        assertNull(formData.maxDifferenceNumberError)
    }

    @Test
    fun similarUsersFilterFormData_correctCustomRanges_isValid() {
        listOf("-10" to "-5", "3" to "", "" to "-3", "0" to "0", "-2" to "7").forEach { (min, max) ->
            val formData = customRangeFormData.copy(minDifferenceNumber = min, maxDifferenceNumber = max)

            assertTrue("$min..$max", formData.isValid)
            assertNull(formData.minDifferenceNumberError)
            assertNull(formData.maxDifferenceNumberError)
        }
    }

    @Test
    fun similarUsersFilterFormData_emptyCustomRange_isNotValidWithEmptyRangeError() {
        assertFalse(customRangeFormData.isValid)
        assertTrue(customRangeFormData.isEmptyDifferenceRange)
        assertNull(customRangeFormData.minDifferenceNumberError)
        assertEquals(emptyRangeError, customRangeFormData.maxDifferenceNumberError)
    }

    @Test
    fun similarUsersFilterFormData_notEmptyOrDefaultRange_isNotEmptyRange() {
        assertFalse(emptyFormData.isEmptyDifferenceRange)
        assertFalse(customRangeFormData.copy(minDifferenceNumber = "1").isEmptyDifferenceRange)
        assertFalse(customRangeFormData.copy(maxDifferenceNumber = "1").isEmptyDifferenceRange)
    }

    @Test
    fun similarUsersFilterFormData_minGreaterThanMax_isNotValidWithLessThanMinError() {
        val formData = customRangeFormData.copy(minDifferenceNumber = "5", maxDifferenceNumber = "-5")

        assertFalse(formData.isValid)
        assertNull(formData.minDifferenceNumberError)
        assertEquals(lessThanMinError, formData.maxDifferenceNumberError)
    }

    @Test
    fun similarUsersFilterFormData_notIntegerBorders_isNotValidWithIntegerErrors() {
        listOf("1.5", "abc", " 5", "-", "99999999999").forEach {
            val minFormData = customRangeFormData.copy(minDifferenceNumber = it)
            val maxFormData = customRangeFormData.copy(maxDifferenceNumber = it)

            assertFalse(it, minFormData.isValid)
            assertEquals(integerError, minFormData.minDifferenceNumberError)
            assertFalse(it, maxFormData.isValid)
            assertEquals(integerError, maxFormData.maxDifferenceNumberError)
        }
    }

    @Test
    fun similarUsersFilterFormData_incorrectBordersWithDefaultRange_isValidWithoutActiveFilters() {
        val formData = emptyFormData.copy(minDifferenceNumber = "abc", maxDifferenceNumber = "-")

        assertTrue(formData.isValid)
        assertEquals(0, formData.activeFiltersNumber)
        assertNull(formData.minDifferenceNumberError)
        assertNull(formData.maxDifferenceNumberError)
    }

    @Test
    fun similarUsersFilterFormData_customRange_countedAsActiveFilterEvenIfIncorrect() {
        assertEquals(1, customRangeFormData.activeFiltersNumber)
    }

    @Test
    fun similarUsersFilterFormData_narrowedAgeRange_countedAsActiveFilter() {
        assertEquals(1, emptyFormData.copy(ageRange = AGE_MIN..30).activeFiltersNumber)
        assertEquals(1, emptyFormData.copy(ageRange = 20..FILTER_AGE_MAX).activeFiltersNumber)
        assertEquals(1, emptyFormData.copy(ageRange = AGE_MIN..AGE_MAX).activeFiltersNumber)
    }

    @Test
    fun similarUsersFilterFormData_toggleTwice_addedAndRemovedValue() {
        val toggledFormData = emptyFormData
            .toggleCategory(1)
            .toggleGender(FEMALE)
            .togglePurpose(RELATIONSHIPS)

        assertEquals(setOf(1), toggledFormData.categoryIds)
        assertEquals(setOf(FEMALE), toggledFormData.genders)
        assertEquals(setOf(RELATIONSHIPS), toggledFormData.purposes)

        val untoggledFormData = toggledFormData
            .toggleCategory(1)
            .toggleGender(FEMALE)
            .togglePurpose(RELATIONSHIPS)

        assertEquals(emptyFormData, untoggledFormData)
    }

    @Test
    fun toSimilarUsersFilterDetails_allData_mapsEveryFilter() {
        assertEquals(FakeData.similarUsersFilterDetails, formData.toSimilarUsersFilterDetails())
    }

    @Test
    fun toSimilarUsersFilterDetails_emptyData_mapsEmptyFilter() {
        assertEquals(SimilarUsersFilterDetails(), emptyFormData.toSimilarUsersFilterDetails())
    }

    @Test
    fun toSimilarUsersFilterDetails_defaultRange_mapsAbsentBordersInsteadOfTypedOnes() {
        val details = formData.copy(differenceRangeType = DEFAULT).toSimilarUsersFilterDetails()

        assertEquals(null to null, details.minDifferenceNumber to details.maxDifferenceNumber)
    }

    @Test
    fun toSimilarUsersFilterDetails_customRangeWithOneBorder_mapsOnlyIt() {
        val minDetails = customRangeFormData.copy(minDifferenceNumber = "-3").toSimilarUsersFilterDetails()
        val maxDetails = customRangeFormData.copy(maxDifferenceNumber = "-3").toSimilarUsersFilterDetails()

        assertEquals(-3 to null, minDetails.minDifferenceNumber to minDetails.maxDifferenceNumber)
        assertEquals(null to -3, maxDetails.minDifferenceNumber to maxDetails.maxDifferenceNumber)
    }

    @Test
    fun toSimilarUsersFilterDetails_ageRange_mapsOnlyNarrowedBorders() {
        assertEquals(20 to 30, formData.copy(ageRange = 20..30).mappedAgeBorders())
        assertEquals(null to 30, formData.copy(ageRange = AGE_MIN..30).mappedAgeBorders())
        assertEquals(20 to null, formData.copy(ageRange = 20..FILTER_AGE_MAX).mappedAgeBorders())
        assertEquals(null to null, formData.copy(ageRange = AGE_MIN..FILTER_AGE_MAX).mappedAgeBorders())
    }

    @Test
    fun toSimilarUsersFilterDetails_ageRangeBelowFilterMax_mapsMaxBorder() {
        assertEquals(null to AGE_MAX, formData.copy(ageRange = AGE_MIN..AGE_MAX).mappedAgeBorders())
    }

    @Test
    fun toSimilarUsersFilterDetails_narrowestUnlimitedAgeRange_mapsMinBorderWithoutMax() {
        assertEquals(AGE_MAX to null, formData.copy(ageRange = AGE_MAX..FILTER_AGE_MAX).mappedAgeBorders())
    }

    @Test
    fun toSimilarUsersFilterFormData_allData_mapsEveryFilter() {
        assertEquals(formData, FakeData.similarUsersFilterDetails.toSimilarUsersFilterFormData())
    }

    @Test
    fun toSimilarUsersFilterFormData_emptyData_mapsEmptyFilter() {
        assertEquals(emptyFormData, SimilarUsersFilterDetails().toSimilarUsersFilterFormData())
    }

    @Test
    fun toSimilarUsersFilterFormData_absentAgeMax_mapsFilterMaxAge() {
        assertEquals(
            25..FILTER_AGE_MAX,
            SimilarUsersFilterDetails(ageMin = 25).toSimilarUsersFilterFormData().ageRange
        )
    }

    @Test
    fun toSimilarUsersFilterFormData_oneBorder_mapsCustomRange() {
        assertEquals(
            customRangeFormData.copy(maxDifferenceNumber = "-3"),
            SimilarUsersFilterDetails(maxDifferenceNumber = -3).toSimilarUsersFilterFormData()
        )
    }

    private fun SimilarUsersFilterFormData.mappedAgeBorders() =
        toSimilarUsersFilterDetails().run { ageMin to ageMax }
}
