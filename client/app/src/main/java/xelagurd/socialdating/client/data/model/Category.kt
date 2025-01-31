package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Serializable
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    override val id: Int,
    val name: String
) : DataEntity