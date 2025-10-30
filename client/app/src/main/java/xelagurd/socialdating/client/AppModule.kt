package xelagurd.socialdating.client

import javax.inject.Named
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import android.content.Context
import androidx.credentials.CredentialManager
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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import xelagurd.socialdating.client.data.local.AppDatabase
import xelagurd.socialdating.client.data.remote.ApiService
import xelagurd.socialdating.client.data.remote.AuthApiService
import xelagurd.socialdating.client.data.remote.AuthInterceptor

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
    fun provideCredentialManager(@ApplicationContext context: Context) =
        CredentialManager.create(context)

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/v1/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAuthRetrofitService(@Named("auth") retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/v1/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRetrofitService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room
        .databaseBuilder(context, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration(false)
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

    @Provides
    fun provideUserStatementsDao(database: AppDatabase) = database.userStatementsDao()
}
