package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidId
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPercent
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

@Serializable
@Entity(
    tableName = "user_defining_themes",
    foreignKeys = [
        ForeignKey(
            entity = DefiningTheme::class,
            parentColumns = ["id"],
            childColumns = ["definingThemeId"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ],
    indices = [
        Index(value = ["definingThemeId", "userId"], unique = true)
    ]
)
data class UserDefiningTheme(
    @PrimaryKey
    override val id: Int,
    val value: Int,
    val interest: Int,
    val userId: Int,
    val definingThemeId: Int
) : DataEntity {
    init {
        require(value.isValidPercent()) { "Value must be between $PERCENT_MIN and $PERCENT_MAX" }
        require(interest.isValidPercent()) { "Interest must be between $PERCENT_MIN and $PERCENT_MAX" }
        require(userId.isValidId()) { "UserId must be at least $ID_MIN" }
        require(definingThemeId.isValidId()) { "DefiningThemeId must be at least $ID_MIN" }
    }

    fun toUserDefiningThemeWithData(definingTheme: DefiningTheme?) =
        definingTheme?.let {
            UserDefiningThemeWithData(
                id = id,
                value = value,
                interest = interest,
                categoryId = it.categoryId,
                definingThemeId = definingThemeId,
                definingThemeName = it.name,
                definingThemeFromOpinion = it.fromOpinion,
                definingThemeToOpinion = it.toOpinion,
                definingThemeNumberInCategory = it.numberInCategory
            )
        }
}