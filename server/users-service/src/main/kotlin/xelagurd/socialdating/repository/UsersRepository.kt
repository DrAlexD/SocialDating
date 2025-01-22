package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xelagurd.socialdating.dto.User

@Repository
interface UsersRepository : CrudRepository<User, Int>
