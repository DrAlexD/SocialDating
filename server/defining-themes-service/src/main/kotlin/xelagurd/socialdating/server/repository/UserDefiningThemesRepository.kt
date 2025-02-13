package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.UserDefiningTheme

interface UserDefiningThemesRepository : JpaRepository<UserDefiningTheme, Int> {
    fun findAllByUserCategoryIdIn(userCategoryIds: Iterable<Int>): List<UserDefiningTheme>

    fun findByUserCategoryIdAndDefiningThemeId(userCategoryId: Int, definingThemeId: Int): UserDefiningTheme?
}