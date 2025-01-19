package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.local.dao.UsersDao
import xelagurd.socialdating.data.model.User

@Singleton
class LocalUsersRepository @Inject constructor(
    private val usersDao: UsersDao
) {
    fun getUsers() =
        usersDao.getUsers()

    fun getUser(userId: Int) =
        usersDao.getUser(userId)

    suspend fun insertUser(user: User) =
        usersDao.insertUser(user)
}