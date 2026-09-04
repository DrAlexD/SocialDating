package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class DetailedSimilarUserDto(
    val similarNumber: Int,
    val oppositeNumber: Int,
    val categories: Map<Int, DetailedSimilarCategoryDto>
)