package xelagurd.socialdating.server.model.additional

data class DefiningThemeResponse(
    val id: Int,
    val name: String,
    val fromOpinion: String,
    val toOpinion: String,
    val categoryId: Int,
    val numberInCategory: Int,
    val orderNumber: Int
)
