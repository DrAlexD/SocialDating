package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable

@Serializable
data class DetailedSimilarUser(
    val similarNumber: Int,
    val oppositeNumber: Int,
    val categories: Map<Int, DetailedSimilarCategory>
)