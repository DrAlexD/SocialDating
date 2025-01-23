package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.UserDefiningTheme

interface UserDefiningThemesRepository : CrudRepository<UserDefiningTheme, Int> {
    fun findAllByUserCategoryIdIn(userCategoryIds: Iterable<Int>): Iterable<UserDefiningTheme>
}