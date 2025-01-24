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
    indices = [Index(value = ["definingThemeId"]), Index(value = ["userId"])]
)
data class Statement(
    @PrimaryKey
    override val id: Int,
    val text: String,
    val isSupportDefiningTheme: Boolean,
    val definingThemeId: Int,
    val creatorUserId: Int
) : DataEntity