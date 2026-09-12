package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val content: List<T>,
    val nextCursor: String? = null
)
