package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData

@Serializable
@Entity(
    tableName = "user_categories",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ],
    indices = [
        Index(value = ["categoryId", "userId"], unique = true)
    ]
)
data class UserCategory(
    @PrimaryKey
    override val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int
) : DataEntity {
    fun toUserCategoryWithData(category: Category?) =
        category?.let {
            UserCategoryWithData(
                id = id,
                interest = interest,
                userId = userId,
                categoryId = categoryId,
                categoryName = category.name
            )
        }
}