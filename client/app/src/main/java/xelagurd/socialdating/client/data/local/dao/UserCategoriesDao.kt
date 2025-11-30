package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.ui.UserCategoryWithData

@Dao
interface UserCategoriesDao {
    @Query(
        """
        select uc.id,
               uc.interest,
               uc.userId,
               uc.categoryId,
               c.name as categoryName
        from user_categories uc
        join categories c on uc.categoryId = c.id
        where userId = :userId
        """
    )
    fun getUserCategories(userId: Int): Flow<List<UserCategoryWithData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCategories(userCategories: List<UserCategory>)

    @Query("delete from user_categories")
    suspend fun deleteAll()
}