package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: Int,
    val name: String
)