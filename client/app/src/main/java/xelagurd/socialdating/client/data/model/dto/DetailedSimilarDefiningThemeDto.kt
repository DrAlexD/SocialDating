package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.SimilarityType

@Serializable
data class DetailedSimilarDefiningThemeDto(
    val id: Int,
    val similarityType: SimilarityType
)