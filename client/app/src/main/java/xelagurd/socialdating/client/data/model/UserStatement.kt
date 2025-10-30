package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.enums.StatementReactionType

@Serializable
@Entity(
    tableName = "user_statements",
    foreignKeys = [
        ForeignKey(
            entity = Statement::class,
            parentColumns = ["id"],
            childColumns = ["statementId"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ],
    indices = [
        Index(value = ["statementId", "userId"], unique = true)
    ]
)
data class UserStatement(
    @PrimaryKey
    override val id: Int,
    val reactionType: StatementReactionType,
    val userId: Int,
    val statementId: Int
) : DataEntity