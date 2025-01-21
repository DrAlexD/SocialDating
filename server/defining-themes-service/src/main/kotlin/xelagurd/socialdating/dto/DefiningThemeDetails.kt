package xelagurd.socialdating.dto

data class DefiningThemeDetails(
    val name: String,
    val fromOpinion: String,
    val toOpinion: String,
    val categoryId: Int
) {
    fun toDefiningTheme(): DefiningTheme =
        DefiningTheme(
            name = name,
            fromOpinion = fromOpinion,
            toOpinion = toOpinion,
            categoryId = categoryId
        )
}