package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.DefiningTheme

interface DefiningThemesRepository : JpaRepository<DefiningTheme, Int> {
    fun findAllByCategoryIdIn(categoryIds: Iterable<Int>): List<DefiningTheme>
}