package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.local.dao.StatementsDao
import xelagurd.socialdating.data.model.Statement

@Singleton
class LocalStatementsRepository @Inject constructor(
    private val statementsDao: StatementsDao
) {
    fun getStatements(definingThemeIds: List<Int>) =
        statementsDao.getStatements(definingThemeIds)

    suspend fun insertStatements(statements: List<Statement>) =
        statementsDao.insertStatements(statements)

    suspend fun insertStatement(statement: Statement) =
        statementsDao.insertStatement(statement)
}