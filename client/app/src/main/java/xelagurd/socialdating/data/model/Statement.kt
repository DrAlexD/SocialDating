package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Serializable
@Entity(
    tableName = "statements",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Statement(
    @PrimaryKey
    val id: Int,
    val text: String,
    val categoryId: Int
)