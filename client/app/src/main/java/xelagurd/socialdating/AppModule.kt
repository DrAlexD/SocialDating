package xelagurd.socialdating

import javax.inject.Singleton
import kotlinx.serialization.json.Json
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import xelagurd.socialdating.data.local.AppDatabase
import xelagurd.socialdating.data.remote.ApiService

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("social-dating-app")
    }

    @Provides
    @Singleton
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl("https://social-dating.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCategoriesDao(database: AppDatabase) = database.categoriesDao()

    @Provides
    fun provideDefiningThemesDao(database: AppDatabase) = database.definingThemesDao()

    @Provides
    fun provideStatementsDao(database: AppDatabase) = database.statementsDao()

    @Provides
    fun provideUsersDao(database: AppDatabase) = database.usersDao()

    @Provides
    fun provideUserCategoriesDao(database: AppDatabase) = database.userCategoriesDao()

    @Provides
    fun provideUserDefiningThemesDao(database: AppDatabase) = database.userDefiningThemesDao()
}
