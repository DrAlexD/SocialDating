package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.Statement

@Dao
interface StatementsDao {
    // FixMe: remove after adding server hosting
    @Query("select * from statements")
    fun getStatements(): Flow<List<Statement>>

    @Query(
        """
        select stm.*
        from statements stm
        join defining_themes dt on stm.definingThemeId = dt.id
        left join user_statements ustm on stm.id = ustm.statementId and ustm.userId = :userId
        where categoryId = :categoryId and ustm.id is null
        """
    )
    fun getStatements(userId: Int, categoryId: Int): Flow<List<Statement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatements(statements: List<Statement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatement(statement: Statement)
}