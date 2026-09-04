package xelagurd.socialdating.server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.dto.UserDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.repository.UsersRepository

@Service
class UsersService(
    private val usersRepository: UsersRepository
) {

    fun getUser(userId: Int) =
        usersRepository.findByIdOrNull(userId)?.toUserDto()

    fun getUsers(userIds: List<Int>): List<UserDto> {
        val language = AppLanguage.current()

        return usersRepository.findAllByIdIn(userIds).map { it.toUserDto(language) }
    }
}