package xelagurd.socialdating.client.data.local

import androidx.room.TypeConverter
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose
import xelagurd.socialdating.client.data.model.enums.Role

class TypeConverter {
    @TypeConverter
    fun fromGender(gender: Gender) = gender.name

    @TypeConverter
    fun toGender(gender: String) = Gender.valueOf(gender)

    @TypeConverter
    fun fromPurpose(purpose: Purpose) = purpose.name

    @TypeConverter
    fun toPurpose(purpose: String) = Purpose.valueOf(purpose)

    @TypeConverter
    fun fromRole(role: Role) = role.name

    @TypeConverter
    fun toRole(role: String) = Role.valueOf(role)
}