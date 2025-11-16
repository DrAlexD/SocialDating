package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.UserStatement

interface UserStatementsRepository : JpaRepository<UserStatement, Int>