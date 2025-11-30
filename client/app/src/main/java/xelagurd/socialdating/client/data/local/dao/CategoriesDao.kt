package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.Category

@Dao
interface CategoriesDao {
    @Query("select * from categories")
    fun getCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    // FixMe: remove after adding server hosting
    @Query("delete from categories")
    suspend fun deleteAll()
}