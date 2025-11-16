package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import androidx.room.Transaction
import xelagurd.socialdating.client.data.local.dao.StatementsDao
import xelagurd.socialdating.client.data.model.Statement

@Singleton
class LocalStatementsRepository @Inject constructor(
    private val statementsDao: StatementsDao
) {
    fun getStatements() =
        statementsDao.getStatements()

    fun getStatements(categoryId: Int) =
        statementsDao.getStatements(categoryId)

    suspend fun insertStatements(statements: List<Statement>) =
        statementsDao.insertStatements(statements)

    @Transaction
    suspend fun replaceStatements(categoryId: Int, statements: List<Statement>) {
        statementsDao.deleteStatements(categoryId)
        statementsDao.insertStatements(statements)
    }

    suspend fun deleteStatement(statement: Statement) =
        statementsDao.deleteStatement(statement)

}