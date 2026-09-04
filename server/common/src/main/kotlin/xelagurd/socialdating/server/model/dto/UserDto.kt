package xelagurd.socialdating.server.model.dto

import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role

data class UserDto(
    val id: Int,
    val name: String,
    val gender: Gender,
    val username: String,
    val age: Int,
    val city: String,
    val purpose: Purpose,
    val activity: Int,
    val role: Role
)
