package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.data.model.DefiningTheme

@Singleton
class LocalDefiningThemesRepository @Inject constructor(
    private val definingThemesDao: DefiningThemesDao
) {
    fun getDefiningThemes() =
        definingThemesDao.getDefiningThemes()

    fun getDefiningThemes(categoryId: Int) =
        definingThemesDao.getDefiningThemes(categoryId)

    fun getDefiningThemes(categoryIds: List<Int>) =
        definingThemesDao.getDefiningThemes(categoryIds)

    suspend fun insertDefiningThemes(definingThemes: List<DefiningTheme>) =
        definingThemesDao.insertDefiningThemes(definingThemes)
}