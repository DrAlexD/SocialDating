package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.PrimaryKey
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val gender: Gender,
    val username: String,
    val password: String,
    val email: String,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val activity: Int
) : DataEntity