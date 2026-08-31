package xelagurd.socialdating.client.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText

@Entity(
    tableName = "statements",
    indices = [
        Index(value = ["text"], unique = true)
    ]
)
data class Statement(
    @PrimaryKey
    override val id: Int,
    val text: String,
    val creatorUserId: Int
) : DataEntity {
    init {
        require(text.isValidText(STATEMENT_TEXT_LENGTH_MIN, STATEMENT_TEXT_LENGTH_MAX)) {
            "Text length must be between $STATEMENT_TEXT_LENGTH_MIN and $STATEMENT_TEXT_LENGTH_MAX"
        }
        require(creatorUserId.isValidId()) { "CreatorUserId must be at least $ID_MIN" }
    }
}
