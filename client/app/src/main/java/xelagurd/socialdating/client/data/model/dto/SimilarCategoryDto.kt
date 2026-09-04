package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SimilarCategoryDto(
    val name: String,
    val differenceNumber: Int
) {
    override fun toString() = "$name($differenceNumber)"
}