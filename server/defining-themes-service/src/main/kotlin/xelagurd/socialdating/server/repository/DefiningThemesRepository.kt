package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.server.model.DefiningTheme

interface DefiningThemesRepository : CrudRepository<DefiningTheme, Int> {
    fun findAllByCategoryIdIn(categoryIds: Iterable<Int>): Iterable<DefiningTheme>
}