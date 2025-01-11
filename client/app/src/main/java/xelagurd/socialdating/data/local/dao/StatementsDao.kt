package xelagurd.socialdating.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.data.model.Statement

@Dao
interface StatementsDao {
    @Query("SELECT * FROM statements WHERE definingThemeId IN (:definingThemeIds)")
    fun getStatements(definingThemeIds: List<Int>): Flow<List<Statement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatements(statements: List<Statement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatement(statement: Statement)
}