package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.User

@Dao
interface UsersDao {
    // FixMe: remove after adding server hosting
    @Query("select * from users")
    fun getUsers(): Flow<List<User>>

    @Query("select * from users where id = :userId")
    fun getUser(userId: Int): Flow<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
}