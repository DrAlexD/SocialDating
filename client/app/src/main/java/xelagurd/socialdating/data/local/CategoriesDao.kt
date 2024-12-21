package xelagurd.socialdating.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.Category

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    suspend fun getCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(item: Category)
}