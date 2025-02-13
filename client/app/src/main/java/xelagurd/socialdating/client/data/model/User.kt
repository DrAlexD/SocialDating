package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.Role

@Serializable
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val gender: Gender,
    val username: String,
    val password: String,
    val email: String?,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val activity: Int,
    val role: Role
) : DataEntity