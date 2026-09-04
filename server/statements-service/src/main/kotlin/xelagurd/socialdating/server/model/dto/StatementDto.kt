package xelagurd.socialdating.server.model.dto

data class StatementDto(
    val id: Int,
    val text: String,
    val creatorUserId: Int,
    val definingThemes: List<DefiningThemeReactionDto>
)
