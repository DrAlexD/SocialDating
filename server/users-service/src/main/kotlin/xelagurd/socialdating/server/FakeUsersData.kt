package xelagurd.socialdating.server

import java.time.LocalDate
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.AppLanguage.ENGLISH
import xelagurd.socialdating.server.model.enums.Gender.MALE
import xelagurd.socialdating.server.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.server.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.server.model.enums.Role.ADMIN
import xelagurd.socialdating.server.model.enums.Role.USER

object FakeUsersData {
    val users = listOf(
        User(
            id = 1,
            nameEn = "Alexander",
            nameRu = "Александр",
            gender = MALE,
            username = "username1",
            password = $$"$2a$10$VlL1TDtoHSflx3dUMswP1eJ24xh5IgRVll5JHp9a24mpsArAZQjnm",
            telegram = "@telegram1",
            birthDate = LocalDate.of(2000, 3, 15),
            cityEn = "Moscow",
            cityRu = "Москва",
            purpose = ALL_AT_ONCE,
            activity = 75,
            role = ADMIN
        ),
        User(
            id = 2,
            nameEn = "Denis",
            nameRu = "Денис",
            gender = MALE,
            username = "username2",
            password = $$"$2a$10$pNja5Q6J680N2HYc2VC7wOza5WoFO6O8e65D9Qwom7OjZkNE3uLe6",
            telegram = "@telegram2",
            birthDate = LocalDate.of(1999, 6, 20),
            cityEn = "Moscow",
            cityRu = "Москва",
            purpose = FRIENDS,
            activity = 75,
            role = USER
        ),
        User(
            id = 3,
            nameEn = "Andrey",
            nameRu = "Андрей",
            gender = MALE,
            username = "username3",
            password = $$"$2a$10$wfn84flRD.QlmGV4.EGVeO9fXm8sDl9u8.0Cq0MwIq67lgTswjL.e",
            telegram = "@telegram3",
            birthDate = LocalDate.of(1999, 1, 25),
            cityEn = "Moscow",
            cityRu = "Москва",
            purpose = RELATIONSHIPS,
            activity = 75,
            role = USER
        )
    )

    val userDtos = users.map { it.toUserDto(ENGLISH) }

    fun List<User>.toUsersWithNullIds() =
        this.map {
            User(
                nameEn = it.nameEn,
                nameRu = it.nameRu,
                gender = it.gender,
                username = it.username,
                password = it.password,
                telegram = it.telegram,
                birthDate = it.birthDate,
                cityEn = it.cityEn,
                cityRu = it.cityRu,
                purpose = it.purpose,
                activity = it.activity,
                role = it.role
            )
        }
}