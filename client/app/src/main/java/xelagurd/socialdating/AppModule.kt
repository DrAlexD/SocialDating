package xelagurd.socialdating

import javax.inject.Singleton
import kotlinx.serialization.json.Json
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import xelagurd.socialdating.data.network.ApiService
import xelagurd.socialdating.data.network.RemoteCategoriesRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService() = Retrofit.Builder()
        .baseUrl("https://social-dating.com/")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRemoteCategoriesRepository(apiService: ApiService): RemoteCategoriesRepository {
        return RemoteCategoriesRepository(apiService)
    }
}
