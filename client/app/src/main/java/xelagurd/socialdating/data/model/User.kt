package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val name: String,
    val gender: String,
    val nickname: String,
    val password: String,
    val email: String,
    val age: Int,
    val city: String,
    val purpose: String,
    val activity: Int
)