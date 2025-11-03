package xelagurd.socialdating.server

import xelagurd.socialdating.server.model.User
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
            name = "Alexander",
            gender = MALE,
            username = "username1",
            password = $$"$2a$10$VlL1TDtoHSflx3dUMswP1eJ24xh5IgRVll5JHp9a24mpsArAZQjnm",
            email = "email1@gmail.com",
            age = 26,
            city = "Moscow",
            purpose = ALL_AT_ONCE,
            activity = 75,
            role = ADMIN
        ),
        User(
            id = 2,
            name = "Denis",
            gender = MALE,
            username = "username2",
            password = $$"$2a$10$pNja5Q6J680N2HYc2VC7wOza5WoFO6O8e65D9Qwom7OjZkNE3uLe6",
            email = "email2@gmail.com",
            age = 27,
            city = "Moscow",
            purpose = FRIENDS,
            activity = 75,
            role = USER
        ),
        User(
            id = 3,
            name = "Andrey",
            gender = MALE,
            username = "username3",
            password = $$"$2a$10$wfn84flRD.QlmGV4.EGVeO9fXm8sDl9u8.0Cq0MwIq67lgTswjL.e",
            email = "email3@gmail.com",
            age = 27,
            city = "Moscow",
            purpose = RELATIONSHIPS,
            activity = 75,
            role = USER
        ),
    )

    fun List<User>.withNullIds(): List<User> =
        this.map {
            User(
                name = it.name,
                gender = it.gender,
                username = it.username,
                password = it.password,
                email = it.email,
                age = it.age,
                city = it.city,
                purpose = it.purpose,
                activity = it.activity,
                role = it.role
            )
        }
}