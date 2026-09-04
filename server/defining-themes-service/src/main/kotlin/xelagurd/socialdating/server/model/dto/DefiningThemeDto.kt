package xelagurd.socialdating.server.model.dto

data class DefiningThemeDto(
    val id: Int,
    val name: String,
    val fromOpinion: String,
    val toOpinion: String,
    val categoryId: Int,
    val numberInCategory: Int,
    val orderNumber: Int
)
