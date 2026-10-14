package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.validation.ValidRange

@ValidRange(min = "ageMin", max = "ageMax")
data class UsersFilterDetails(
    val genders: List<Gender>? = null,

    @field:Min(AGE_MIN.toLong())
    @field:Max(AGE_MAX.toLong())
    val ageMin: Int? = null,

    @field:Min(AGE_MIN.toLong())
    @field:Max(AGE_MAX.toLong())
    val ageMax: Int? = null,

    val purposes: List<Purpose>? = null,

    @field:Positive
    val sameCityUserId: Int? = null
)
