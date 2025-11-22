package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable

@Serializable
data class SimilarCategory(
    val name: String,
    val differenceNumber: Int
) {
    override fun toString() = "$name($differenceNumber)"
}