package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import xelagurd.socialdating.server.model.User

@Repository
interface UsersRepository : JpaRepository<User, Int>
