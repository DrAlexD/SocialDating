package xelagurd.socialdating.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.additional.UserDefiningThemeWithData

@Dao
interface UserDefiningThemesDao {
    @Query("""
        SELECT 
            udt.id AS id,
            udt.value AS value,
            udt.interest AS interest,
            udt.userCategoryId AS userCategoryId,
            udt.definingThemeId AS definingThemeId,
            dt.name AS definingThemeName,
            dt.fromOpinion AS definingThemeFromOpinion,
            dt.toOpinion AS definingThemeToOpinion
        FROM user_defining_themes AS udt
        INNER JOIN defining_themes AS dt ON udt.definingThemeId = dt.id
        WHERE udt.userCategoryId IN (:userCategoryIds)
    """)
    fun getUserDefiningThemes(userCategoryIds: List<Int>): Flow<List<UserDefiningThemeWithData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDefiningThemes(userDefiningThemes: List<UserDefiningTheme>)
}