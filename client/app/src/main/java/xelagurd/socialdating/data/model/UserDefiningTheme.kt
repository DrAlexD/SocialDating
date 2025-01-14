package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
            entity = UserCategory::class,
            parentColumns = ["id"],
            childColumns = ["userCategoryId"]
        )
    ],
    indices = [Index(value = ["definingThemeId"]), Index(value = ["userCategoryId"])]
)
data class UserDefiningTheme(
    @PrimaryKey
    override val id: Int,
    val value: Int,
    val interest: Int,
    val userCategoryId: Int,
    val definingThemeId: Int
) : DataEntity