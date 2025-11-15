package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
        Index(value = ["name"], unique = true)
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
) : DataEntity