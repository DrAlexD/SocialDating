package xelagurd.socialdating

import javax.inject.Singleton
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import xelagurd.socialdating.data.local.AppDatabase
import xelagurd.socialdating.data.network.ApiService

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideRetrofitService(): ApiService = FakeApiService()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room
        .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideCategoriesDao(database: AppDatabase) = database.categoriesDao()

    @Provides
    fun provideStatementsDao(database: AppDatabase) = database.statementsDao()
}
