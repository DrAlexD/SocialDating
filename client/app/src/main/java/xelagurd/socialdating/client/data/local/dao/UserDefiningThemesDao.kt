package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.ui.UserDefiningThemeWithData

@Dao
interface UserDefiningThemesDao {
    // FixMe: remove after adding server hosting
    @Query("select * from user_defining_themes")
    fun getUserDefiningThemes(): Flow<List<UserDefiningTheme>>

    @Query(
        """
        select udt.id,
               udt.value,
               udt.interest,
               dt.categoryId,
               udt.definingThemeId,
               dt.name as definingThemeName,
               dt.fromOpinion as definingThemeFromOpinion,
               dt.toOpinion as definingThemeToOpinion
        from user_defining_themes udt
        join defining_themes dt on udt.definingThemeId = dt.id
        where userId = :userId
        """
    )
    fun getUserDefiningThemes(userId: Int): Flow<List<UserDefiningThemeWithData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDefiningThemes(userDefiningThemes: List<UserDefiningTheme>)
}