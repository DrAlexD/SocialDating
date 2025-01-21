package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.DefiningTheme

interface DefiningThemesRepository : CrudRepository<DefiningTheme, Int> {
    fun findAllByCategoryIdIn(categoryIds: Iterable<Int>): Iterable<DefiningTheme>
}