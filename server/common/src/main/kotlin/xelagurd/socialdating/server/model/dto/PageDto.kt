package xelagurd.socialdating.server.model.dto

data class PageDto<T>(
    val content: List<T>,
    val nextCursor: String? = null
)
