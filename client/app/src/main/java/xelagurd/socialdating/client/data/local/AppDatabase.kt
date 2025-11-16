package xelagurd.socialdating.client.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xelagurd.socialdating.client.data.local.dao.CategoriesDao
import xelagurd.socialdating.client.data.local.dao.DefiningThemesDao
import xelagurd.socialdating.client.data.local.dao.StatementsDao
import xelagurd.socialdating.client.data.local.dao.UserCategoriesDao
import xelagurd.socialdating.client.data.local.dao.UserDefiningThemesDao
import xelagurd.socialdating.client.data.local.dao.UsersDao
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme

@Database(
    entities = [
        Category::class,
        DefiningTheme::class,
        Statement::class,
        User::class,
        UserCategory::class,
        UserDefiningTheme::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriesDao(): CategoriesDao

    abstract fun definingThemesDao(): DefiningThemesDao

    abstract fun statementsDao(): StatementsDao

    abstract fun usersDao(): UsersDao

    abstract fun userCategoriesDao(): UserCategoriesDao

    abstract fun userDefiningThemesDao(): UserDefiningThemesDao

}