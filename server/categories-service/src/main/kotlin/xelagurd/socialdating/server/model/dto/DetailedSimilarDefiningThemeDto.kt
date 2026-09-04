package xelagurd.socialdating.server.model.dto

import xelagurd.socialdating.server.model.enums.SimilarityType

data class DetailedSimilarDefiningThemeDto(
    val id: Int,
    val similarityType: SimilarityType
)