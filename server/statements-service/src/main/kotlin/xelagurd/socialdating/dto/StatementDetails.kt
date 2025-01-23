package xelagurd.socialdating.dto

data class StatementDetails(
    val text: String,
    val isSupportDefiningTheme: Boolean,
    val definingThemeId: Int,
    val userId: Int
) {
    fun toStatement(): Statement =
        Statement(
            text = text,
            isSupportDefiningTheme = isSupportDefiningTheme,
            definingThemeId = definingThemeId,
            userId = userId
        )
}