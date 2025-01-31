package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.server.model.UserDefiningTheme

interface UserDefiningThemesRepository : CrudRepository<UserDefiningTheme, Int> {
    fun findAllByUserCategoryIdIn(userCategoryIds: Iterable<Int>): Iterable<UserDefiningTheme>

    fun findByUserCategoryIdAndDefiningThemeId(userCategoryId: Int, definingThemeId: Int): UserDefiningTheme?
}