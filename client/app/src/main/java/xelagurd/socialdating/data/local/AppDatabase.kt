package xelagurd.socialdating.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import xelagurd.socialdating.data.local.dao.CategoriesDao
import xelagurd.socialdating.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.data.local.dao.StatementsDao
import xelagurd.socialdating.data.local.dao.UsersDao
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.User

@Database(
    entities = [
        Category::class,
        DefiningTheme::class,
        Statement::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao

    abstract fun definingThemesDao(): DefiningThemesDao

    abstract fun statementsDao(): StatementsDao

    abstract fun usersDao(): UsersDao
}