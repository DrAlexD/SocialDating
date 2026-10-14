package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidAge
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

@Serializable
data class SimilarUsersFilterDetails(
    val categoryIds: Set<Int> = setOf(),
    val minDifferenceNumber: Int? = null,
    val maxDifferenceNumber: Int? = null,
    val genders: Set<Gender> = setOf(),
    val ageMin: Int? = null,
    val ageMax: Int? = null,
    val purposes: Set<Purpose> = setOf(),
    val isSameCity: Boolean = false
) {
    init {
        require(
            minDifferenceNumber == null || maxDifferenceNumber == null || minDifferenceNumber <= maxDifferenceNumber
        ) { "MinDifferenceNumber must not be greater than MaxDifferenceNumber" }
        require(ageMin == null || ageMin.isValidAge()) { "AgeMin must be between $AGE_MIN and $AGE_MAX" }
        require(ageMax == null || ageMax.isValidAge()) { "AgeMax must be between $AGE_MIN and $AGE_MAX" }
        require(ageMin == null || ageMax == null || ageMin <= ageMax) { "AgeMin must not be greater than AgeMax" }
    }

    fun toJson() = Json.encodeToString(this)

    companion object {
        // the saved filter can be outdated or damaged, then it is replaced by the empty one
        // (the serialization exceptions and the failed requirements are both IllegalArgumentException)
        fun fromJson(json: String?) =
            try {
                json?.let { Json.decodeFromString<SimilarUsersFilterDetails>(it) }
            } catch (_: IllegalArgumentException) {
                null
            } ?: SimilarUsersFilterDetails()
    }
}
