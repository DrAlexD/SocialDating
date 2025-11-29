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
                categoryId = it.categoryId,
                definingThemeId = definingThemeId,
                definingThemeName = it.name,
                definingThemeFromOpinion = it.fromOpinion,
                definingThemeToOpinion = it.toOpinion,
                definingThemeNumberInCategory = it.numberInCategory
            )
        }
}