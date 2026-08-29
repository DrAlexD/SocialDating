package xelagurd.socialdating.client.data.model

import androidx.room.Entity
import androidx.room.ForeignKey

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
)
