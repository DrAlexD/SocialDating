package xelagurd.socialdating.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import xelagurd.socialdating.data.local.dao.CategoriesDao
import xelagurd.socialdating.data.local.dao.StatementsDao
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.Statement

@Database(entities = [Category::class, Statement::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao

    abstract fun statementsDao(): StatementsDao
}