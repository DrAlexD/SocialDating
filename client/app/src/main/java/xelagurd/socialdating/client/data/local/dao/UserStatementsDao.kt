package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.UserStatement

@Dao
interface UserStatementsDao {
    // FixMe: remove after adding server hosting
    @Query("select * from user_statements")
    fun getUserStatements(): Flow<List<UserStatement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStatements(userStatements: List<UserStatement>)
}