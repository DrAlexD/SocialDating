package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import xelagurd.socialdating.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.data.model.DefiningTheme

@Singleton
class LocalDefiningThemesRepository @Inject constructor(
    private val definingThemesDao: DefiningThemesDao
) {
    fun getDefiningThemes(categoryId: Int): Flow<List<DefiningTheme>> =
        definingThemesDao.getDefiningThemes(categoryId)

    suspend fun insertDefiningThemes(definingThemes: List<DefiningTheme>) =
        definingThemesDao.insertDefiningThemes(definingThemes)
}