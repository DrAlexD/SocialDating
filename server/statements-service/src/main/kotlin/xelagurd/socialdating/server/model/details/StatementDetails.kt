package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.DefaultDataProperties.LENGTH_MAX
import xelagurd.socialdating.server.model.Statement

data class StatementDetails(
    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val text: String,

    val isSupportDefiningTheme: Boolean,

    @field:Positive
    val definingThemeId: Int,

    @field:Positive
    val creatorUserId: Int
) {
    fun toStatement() =
        Statement(
            text = text,
            isSupportDefiningTheme = isSupportDefiningTheme,
            definingThemeId = definingThemeId,
            creatorUserId = creatorUserId
        )
}