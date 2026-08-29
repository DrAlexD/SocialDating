package xelagurd.socialdating.client.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "statements",
    indices = [
        Index(value = ["text"], unique = true)
    ]
)
data class Statement(
    @PrimaryKey
    override val id: Int,
    val text: String,
    val creatorUserId: Int
) : DataEntity
