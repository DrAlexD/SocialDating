package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.DefiningTheme

@Dao
interface DefiningThemesDao {
    @Query("select * from defining_themes where categoryId = :categoryId")
    fun getDefiningThemes(categoryId: Int): Flow<List<DefiningTheme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefiningThemes(definingThemes: List<DefiningTheme>)

    // FixMe: remove after adding server hosting
    @Query("delete from defining_themes")
    suspend fun deleteAll()
}