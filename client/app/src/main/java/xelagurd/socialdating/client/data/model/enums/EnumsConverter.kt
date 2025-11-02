package xelagurd.socialdating.client.data.model.enums

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

    @TypeConverter
    fun fromRole(role: Role): String {
        return role.name
    }

    @TypeConverter
    fun toRole(role: String): Role {
        return Role.valueOf(role)
    }

    @TypeConverter
    fun fromStatementReactionType(statementReactionType: StatementReactionType): String {
        return statementReactionType.name
    }

    @TypeConverter
    fun toStatementReactionType(statementReactionType: String): StatementReactionType {
        return StatementReactionType.valueOf(statementReactionType)
    }
}