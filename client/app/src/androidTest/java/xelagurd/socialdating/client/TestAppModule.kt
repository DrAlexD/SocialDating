package xelagurd.socialdating.client

import java.util.UUID
import javax.inject.Singleton
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import xelagurd.socialdating.client.data.local.AppDatabase
import xelagurd.socialdating.client.data.remote.ApiService
import xelagurd.socialdating.client.data.remote.AuthApiService

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile("social-dating-app-test-${UUID.randomUUID()}")
    }

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context) =
        CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideAuthRetrofitService(): AuthApiService = FakeAuthApiService()

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
