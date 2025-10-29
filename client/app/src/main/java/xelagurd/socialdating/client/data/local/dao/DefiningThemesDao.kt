package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.DefiningTheme

@Dao
interface DefiningThemesDao {
    // FixMe: remove after adding server hosting
    @Query("select * from defining_themes")
    fun getDefiningThemes(): Flow<List<DefiningTheme>>

    @Query("select * from defining_themes where categoryId = :categoryId")
    fun getDefiningThemes(categoryId: Int): Flow<List<DefiningTheme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefiningThemes(categories: List<DefiningTheme>)
}