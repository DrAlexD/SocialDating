package xelagurd.socialdating.server.model.additional

import com.fasterxml.jackson.annotation.JsonIgnore

data class UserWithSimilarity(
    val id: Int,
    val similarityNumber: Int,
    val oppositeNumber: Int,
    @field:JsonIgnore
    val differenceNumber: Int,
    val similarCategories: List<CategoryWithSimilarityDiff>,
    val oppositeCategories: List<CategoryWithSimilarityDiff>
)