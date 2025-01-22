package xelagurd.socialdating.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.User
import xelagurd.socialdating.repository.UsersRepository

@Service
class UsersService(
    private val usersRepository: UsersRepository
) {

    fun getUser(userId: Int): User? {
        return usersRepository.findByIdOrNull(userId)
    }
}