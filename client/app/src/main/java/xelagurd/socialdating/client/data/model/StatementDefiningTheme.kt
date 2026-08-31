package xelagurd.socialdating.client.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId

@Entity(
    tableName = "statement_defining_themes",
    primaryKeys = ["statementId", "definingThemeId"],
    foreignKeys = [
        ForeignKey(
            entity = Statement::class,
            parentColumns = ["id"],
            childColumns = ["statementId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StatementDefiningTheme(
    val statementId: Int,
    val definingThemeId: Int
) {
    init {
        require(statementId.isValidId()) { "StatementId must be at least $ID_MIN" }
        require(definingThemeId.isValidId()) { "DefiningThemeId must be at least $ID_MIN" }
    }
}
