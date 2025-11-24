package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.SimilarityType

@Serializable
data class DetailedSimilarDefiningTheme(
    val id: Int,
    val similarityType: SimilarityType
)