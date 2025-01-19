package xelagurd.socialdating.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.DefiningTheme

@Dao
interface DefiningThemesDao {
    @Query("SELECT * FROM defining_themes")
    fun getDefiningThemes(): Flow<List<DefiningTheme>>

    @Query("SELECT * FROM defining_themes WHERE categoryId = :categoryId")
    fun getDefiningThemes(categoryId: Int): Flow<List<DefiningTheme>>

    @Query("SELECT * FROM defining_themes WHERE categoryId IN (:categoryIds)")
    fun getDefiningThemes(categoryIds: List<Int>): Flow<List<DefiningTheme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefiningThemes(categories: List<DefiningTheme>)
}