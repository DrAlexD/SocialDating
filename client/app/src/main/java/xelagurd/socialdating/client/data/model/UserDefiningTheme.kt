package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

@Serializable
@Entity(
    tableName = "user_defining_themes",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
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
    fun toUserDefiningThemeWithData(definingTheme: DefiningTheme?) =
        definingTheme?.let {
            UserDefiningThemeWithData(
                id = id,
                value = value,
                interest = interest,
                categoryId = definingTheme.categoryId,
                definingThemeId = definingThemeId,
                definingThemeName = definingTheme.name,
                definingThemeFromOpinion = definingTheme.fromOpinion,
                definingThemeToOpinion = definingTheme.toOpinion,
                definingThemeNumberInCategory = definingTheme.numberInCategory
            )
        }
}