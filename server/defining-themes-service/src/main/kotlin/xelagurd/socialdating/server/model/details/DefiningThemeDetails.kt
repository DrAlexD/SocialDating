package xelagurd.socialdating.server.model.details

import xelagurd.socialdating.server.model.DefiningTheme

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