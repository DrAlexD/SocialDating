package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.local.dao.UserStatementsDao
import xelagurd.socialdating.client.data.model.UserStatement

@Singleton
class LocalUserStatementsRepository @Inject constructor(
    private val userStatementsDao: UserStatementsDao
) {
    fun getUserStatements() =
        userStatementsDao.getUserStatements()

    suspend fun insertUserStatements(userStatements: List<UserStatement>) =
        userStatementsDao.insertUserStatements(userStatements)
}