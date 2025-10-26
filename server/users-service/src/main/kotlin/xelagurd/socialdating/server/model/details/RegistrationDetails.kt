package xelagurd.socialdating.server.model.details

import org.springframework.security.crypto.password.PasswordEncoder
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role

data class RegistrationDetails(
    val name: String,
    val gender: Gender,
    val username: String,
    val password: String,
    val repeatedPassword: String,
    val email: String,
    val age: String,
    val city: String,
    val purpose: Purpose
) {
    fun toUser(passwordEncoder: PasswordEncoder): User =
        User(
            name = name,
            gender = gender,
            username = username,
            password = passwordEncoder.encode(password),
            email = email,
            age = age.toInt(),
            city = city,
            purpose = purpose,
            activity = 50,
            role = Role.USER
        )
}