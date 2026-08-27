package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose

data class UserData(
    val id: Int,
    val name: String,
    val gender: Gender,
    val age: Int,
    val city: String,
    val purpose: Purpose
)