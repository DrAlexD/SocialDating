package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.SimilarityType

@Serializable
data class DetailedSimilarCategoryDto(
    val id: Int,
    val similarityType: SimilarityType,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val definingThemes: Map<Int, DetailedSimilarDefiningThemeDto>
)