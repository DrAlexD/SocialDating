package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.client.data.model.DefiningTheme

@Singleton
class LocalDefiningThemesRepository @Inject constructor(
    private val definingThemesDao: DefiningThemesDao
) {
    fun getDefiningThemes(categoryId: Int? = null) =
        definingThemesDao.getDefiningThemes(categoryId)

    suspend fun insertDefiningThemes(definingThemes: List<DefiningTheme>) =
        definingThemesDao.insertDefiningThemes(definingThemes)

    suspend fun deleteAll() =
        definingThemesDao.deleteAll()
}