package xelagurd.socialdating.client.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.StatementDefiningTheme

@Dao
interface StatementsDao {
    // FixMe: remove after adding server hosting
    @Query("select * from statements")
    fun getStatements(): Flow<List<Statement>>

    @Query(
        """
        select distinct stm.*
        from statements stm
        join statement_defining_themes sdt on stm.id = sdt.statementId
        join defining_themes dt on sdt.definingThemeId = dt.id
        where dt.categoryId = :categoryId
        """
    )
    fun getStatements(categoryId: Int): Flow<List<Statement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatements(statements: List<Statement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatementDefiningThemes(statementDefiningThemes: List<StatementDefiningTheme>)

    @Query(
        """
        delete from statements
        where id in (
            select sdt.statementId
            from statement_defining_themes sdt
            join defining_themes dt on sdt.definingThemeId = dt.id
            where dt.categoryId = :categoryId
        )
        """
    )
    suspend fun deleteStatements(categoryId: Int)

    @Delete
    suspend fun deleteStatement(statement: Statement)

    @Query("delete from statements")
    suspend fun deleteAll()
}
