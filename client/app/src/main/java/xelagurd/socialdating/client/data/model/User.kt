package xelagurd.socialdating.client.data.model

import kotlinx.serialization.Serializable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidAge
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPercent
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidUsername
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.Role

@Serializable
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class User(
    @PrimaryKey
    override val id: Int,
    val name: String,
    val gender: Gender,
    val username: String,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val activity: Int,
    val role: Role
) : DataEntity {
    init {
        require(name.isValidText(NAME_LENGTH_MIN, NAME_LENGTH_MAX)) {
            "Name length must be between $NAME_LENGTH_MIN and $NAME_LENGTH_MAX"
        }
        require(username.isValidUsername()) {
            "Username length must be between $USERNAME_LENGTH_MIN and $USERNAME_LENGTH_MAX " +
                    "and match username pattern"
        }
        require(age.isValidAge()) { "Age must be between $AGE_MIN and $AGE_MAX" }
        require(city.isValidText(CITY_LENGTH_MIN, CITY_LENGTH_MAX)) {
            "City length must be between $CITY_LENGTH_MIN and $CITY_LENGTH_MAX"
        }
        require(activity.isValidPercent()) { "Activity must be between $PERCENT_MIN and $PERCENT_MAX" }
    }
}