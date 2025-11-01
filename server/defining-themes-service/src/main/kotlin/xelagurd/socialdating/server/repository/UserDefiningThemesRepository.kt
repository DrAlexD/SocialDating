package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.UserDefiningTheme

interface UserDefiningThemesRepository : JpaRepository<UserDefiningTheme, Int> {
    fun findAllByUserId(userId: Int): List<UserDefiningTheme>

    fun findByUserIdAndDefiningThemeId(userId: Int, definingThemeId: Int): UserDefiningTheme?
}