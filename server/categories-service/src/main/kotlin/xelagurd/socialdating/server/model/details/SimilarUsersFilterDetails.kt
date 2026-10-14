package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.DIFFERENCE_NUMBER_MIN
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.validation.ValidRange

@ValidRange(min = "minDifferenceNumber", max = "maxDifferenceNumber")
@ValidRange(min = "ageMin", max = "ageMax")
data class SimilarUsersFilterDetails(
    val categoryIds: List<Int>? = null,

    val minDifferenceNumber: Int? = null,

    val maxDifferenceNumber: Int? = null,

    val genders: List<Gender>? = null,

    @field:Min(AGE_MIN.toLong())
    @field:Max(AGE_MAX.toLong())
    val ageMin: Int? = null,

    @field:Min(AGE_MIN.toLong())
    @field:Max(AGE_MAX.toLong())
    val ageMax: Int? = null,

    val purposes: List<Purpose>? = null,

    val isSameCity: Boolean = false
) {

    // the default range is used only when no border is set, otherwise the absent border doesn't limit anything
    fun toDifferenceNumberRange() =
        (minDifferenceNumber ?: if (maxDifferenceNumber == null) DIFFERENCE_NUMBER_MIN else Int.MIN_VALUE)..
                (maxDifferenceNumber ?: Int.MAX_VALUE)

    // the users data is stored by users-service, so it is requested only when it is filtered
    fun isUsersDataFiltered() =
        genders != null || ageMin != null || ageMax != null || purposes != null || isSameCity

    fun toUsersFilterDetails(currentUserId: Int) =
        UsersFilterDetails(
            genders = genders,
            ageMin = ageMin,
            ageMax = ageMax,
            purposes = purposes,
            sameCityUserId = currentUserId.takeIf { isSameCity }
        )
}
