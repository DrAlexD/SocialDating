package xelagurd.socialdating.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import xelagurd.socialdating.data.model.Category

@Database(entities = [Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
}