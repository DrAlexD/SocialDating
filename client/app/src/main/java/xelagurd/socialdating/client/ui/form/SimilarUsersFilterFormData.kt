package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.details.SimilarUsersFilterDetails
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType.CUSTOM
import xelagurd.socialdating.client.data.model.enums.DifferenceRangeType.DEFAULT
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.ui.form.FormFieldErrors.integerError
import xelagurd.socialdating.client.ui.form.FormFieldErrors.rangeMaxError

data class SimilarUsersFilterFormData(
    val categoryIds: Set<Int> = setOf(),
    val differenceRangeType: DifferenceRangeType = DEFAULT,
    val minDifferenceNumber: String = "",
    val maxDifferenceNumber: String = "",
    val genders: Set<Gender> = setOf(),
    val ageRange: IntRange = AGE_MIN..AGE_MAX,
    val purposes: Set<Purpose> = setOf(),
    val isSameCity: Boolean = false
) : FormData {
    // every filter is optional, so only the custom range of the difference number can make the filter incorrect
    val isValid
        get() = minDifferenceNumberError == null && maxDifferenceNumberError == null

    // the typed borders are kept with the default range, but they are hidden, so they are not checked
    val minDifferenceNumberError
        get() = when (differenceRangeType) {
            DEFAULT -> null
            CUSTOM -> minDifferenceNumber.integerError()
        }

    val maxDifferenceNumberError
        get() = when (differenceRangeType) {
            DEFAULT -> null
            CUSTOM -> maxDifferenceNumber.rangeMaxError(minDifferenceNumber)
        }

    // the empty range is the error of both borders, but its message is shown only by the max
    val isEmptyDifferenceRange
        get() = differenceRangeType == CUSTOM && minDifferenceNumber.isEmpty() && maxDifferenceNumber.isEmpty()

    val activeFiltersNumber
        get() = listOf(
            categoryIds.isNotEmpty(),
            differenceRangeType == CUSTOM,
            genders.isNotEmpty(),
            ageRange != AGE_MIN..AGE_MAX,
            purposes.isNotEmpty(),
            isSameCity
        ).count { it }

    fun toggleCategory(categoryId: Int) =
        copy(categoryIds = categoryIds.toggle(categoryId))

    fun toggleGender(gender: Gender) =
        copy(genders = genders.toggle(gender))

    fun togglePurpose(purpose: Purpose) =
        copy(purposes = purposes.toggle(purpose))

    // the default range and the whole age range don't need to be sent, so their borders are absent
    fun toSimilarUsersFilterDetails() =
        SimilarUsersFilterDetails(
            categoryIds = categoryIds,
            minDifferenceNumber = minDifferenceNumber.takeIf { differenceRangeType == CUSTOM }?.toIntOrNull(),
            maxDifferenceNumber = maxDifferenceNumber.takeIf { differenceRangeType == CUSTOM }?.toIntOrNull(),
            genders = genders,
            ageMin = ageRange.first.takeIf { it > AGE_MIN },
            ageMax = ageRange.last.takeIf { it < AGE_MAX },
            purposes = purposes,
            isSameCity = isSameCity
        )

    private fun <T> Set<T>.toggle(element: T) =
        if (contains(element)) this - element else this + element
}

fun SimilarUsersFilterDetails.toSimilarUsersFilterFormData() =
    SimilarUsersFilterFormData(
        categoryIds = categoryIds,
        differenceRangeType = if (minDifferenceNumber != null || maxDifferenceNumber != null) CUSTOM else DEFAULT,
        minDifferenceNumber = minDifferenceNumber?.toString().orEmpty(),
        maxDifferenceNumber = maxDifferenceNumber?.toString().orEmpty(),
        genders = genders,
        ageRange = (ageMin ?: AGE_MIN)..(ageMax ?: AGE_MAX),
        purposes = purposes,
        isSameCity = isSameCity
    )
