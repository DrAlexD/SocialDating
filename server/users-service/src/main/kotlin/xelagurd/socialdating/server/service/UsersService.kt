package xelagurd.socialdating.server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.repository.UsersRepository

@Service
class UsersService(
    private val usersRepository: UsersRepository
) {

    fun getUser(userId: Int): User? {
        return usersRepository.findByIdOrNull(userId)
    }
}