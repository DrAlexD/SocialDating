package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import xelagurd.socialdating.server.model.Statement

data class StatementDetails(
    @field:NotBlank
    val text: String,

    val isSupportDefiningTheme: Boolean,

    @field:Min(value = 1)
    val definingThemeId: Int,

    @field:Min(value = 1)
    val creatorUserId: Int
) {
    fun toStatement(): Statement =
        Statement(
            text = text,
            isSupportDefiningTheme = isSupportDefiningTheme,
            definingThemeId = definingThemeId,
            creatorUserId = creatorUserId
        )
}