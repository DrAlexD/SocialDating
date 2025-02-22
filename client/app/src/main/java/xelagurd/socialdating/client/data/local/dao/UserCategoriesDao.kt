package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.additional.UserCategoryWithData

@Dao
interface UserCategoriesDao {
    @Query("SELECT * FROM user_categories")
    fun getUserCategories(): Flow<List<UserCategory>>

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