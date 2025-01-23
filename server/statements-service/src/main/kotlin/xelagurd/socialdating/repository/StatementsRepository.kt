package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.Statement

interface StatementsRepository : CrudRepository<Statement, Int> {
    fun findAllByDefiningThemeIdIn(definingThemeIds: Iterable<Int>): Iterable<Statement>
}