package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText

@Serializable
@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Category(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val orderNumber: Int
) : DataEntity {
    init {
        require(name.isValidText(CATEGORY_NAME_LENGTH_MIN, CATEGORY_NAME_LENGTH_MAX)) {
            "Name length must be between $CATEGORY_NAME_LENGTH_MIN and $CATEGORY_NAME_LENGTH_MAX"
        }
        require(orderNumber.isValidId()) { "OrderNumber must be at least $ID_MIN" }
    }
}