package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

@Serializable
data class SimilarUserWithData(
    override val id: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val similarCategories: List<SimilarCategory>,
    val oppositeCategories: List<SimilarCategory>
) : DataEntity