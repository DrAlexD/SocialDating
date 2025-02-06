package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.Statement

interface StatementsRepository : JpaRepository<Statement, Int> {
    fun findAllByDefiningThemeIdIn(definingThemeIds: Iterable<Int>): List<Statement>
}