package xelagurd.socialdating.server.model.dto

import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose

data class SimilarUserDto(
    val id: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val similarCategories: List<SimilarCategoryDto>,
    val oppositeCategories: List<SimilarCategoryDto>
)