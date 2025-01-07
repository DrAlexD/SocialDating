package xelagurd.socialdating.data.model.enums

import androidx.room.TypeConverter

class EnumsConverter {
    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(gender: String): Gender {
        return Gender.valueOf(gender)
    }

    @TypeConverter
    fun fromPurpose(purpose: Purpose): String {
        return purpose.name
    }

    @TypeConverter
    fun toPurpose(purpose: String): Purpose {
        return Purpose.valueOf(purpose)
    }
}