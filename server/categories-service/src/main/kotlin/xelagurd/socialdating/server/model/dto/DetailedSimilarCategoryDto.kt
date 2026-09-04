package xelagurd.socialdating.server.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import xelagurd.socialdating.server.model.enums.SimilarityType

data class DetailedSimilarCategoryDto(
    val id: Int,
    val similarityType: SimilarityType,
    val similarNumber: Int,
    val oppositeNumber: Int,
    @field:JsonIgnore
    val differenceNumber: Int,
    val definingThemes: Map<Int, DetailedSimilarDefiningThemeDto>
)