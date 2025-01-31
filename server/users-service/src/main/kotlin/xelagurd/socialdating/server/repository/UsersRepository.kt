package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import xelagurd.socialdating.server.model.User

@Repository
interface UsersRepository : CrudRepository<User, Int>
