package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.SimilarityType

data class DetailedSimilarDefiningTheme(
    val id: Int,
    val similarityType: SimilarityType
)