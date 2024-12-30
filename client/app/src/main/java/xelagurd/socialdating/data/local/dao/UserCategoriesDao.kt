package xelagurd.socialdating.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserCategoryWithData

@Dao
interface UserCategoriesDao {
    @Query(
        """
        SELECT 
            uc.id AS id,
            uc.interest AS interest,
            uc.userId AS userId,
            uc.categoryId AS categoryId,
            c.name AS categoryName
        FROM user_categories AS uc
        INNER JOIN categories AS c ON uc.categoryId = c.id
        WHERE uc.userId = :userId
    """
    )
    fun getUserCategories(userId: Int): Flow<List<UserCategoryWithData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCategories(userCategories: List<UserCategory>)
}