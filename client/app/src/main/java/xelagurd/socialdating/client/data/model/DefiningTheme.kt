package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.OPINION_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.OPINION_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText

@Serializable
@Entity(
    tableName = "defining_themes",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ],
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["categoryId", "numberInCategory"], unique = true)
    ]
)
data class DefiningTheme(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val fromOpinion: String,
    val toOpinion: String,
    val categoryId: Int,
    val numberInCategory: Int
) : DataEntity {
    init {
        require(name.isValidText(DEFINING_THEME_NAME_LENGTH_MIN, DEFINING_THEME_NAME_LENGTH_MAX)) {
            "Name length must be between $DEFINING_THEME_NAME_LENGTH_MIN and $DEFINING_THEME_NAME_LENGTH_MAX"
        }
        require(fromOpinion.isValidText(OPINION_LENGTH_MIN, OPINION_LENGTH_MAX)) {
            "FromOpinion length must be between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX"
        }
        require(toOpinion.isValidText(OPINION_LENGTH_MIN, OPINION_LENGTH_MAX)) {
            "ToOpinion length must be between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX"
        }
        require(categoryId.isValidId()) { "CategoryId must be at least $ID_MIN" }
        require(numberInCategory.isValidId()) { "NumberInCategory must be at least $ID_MIN" }
    }
}