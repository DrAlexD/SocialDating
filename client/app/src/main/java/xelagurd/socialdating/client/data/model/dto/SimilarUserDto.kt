package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.data.model.dto.SimilarCategoryDto
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

@Serializable
data class SimilarUserDto(
    override val id: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val similarCategories: List<SimilarCategoryDto>,
    val oppositeCategories: List<SimilarCategoryDto>
) : DataEntity