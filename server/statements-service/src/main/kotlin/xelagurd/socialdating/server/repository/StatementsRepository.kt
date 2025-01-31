package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.server.model.Statement

interface StatementsRepository : CrudRepository<Statement, Int> {
    fun findAllByDefiningThemeIdIn(definingThemeIds: Iterable<Int>): Iterable<Statement>
}