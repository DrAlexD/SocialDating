package xelagurd.socialdating.data.local

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.Category

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    fun getCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(item: List<Category>)
}