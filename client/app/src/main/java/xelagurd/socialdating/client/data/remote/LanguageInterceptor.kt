package xelagurd.socialdating.client.data.remote

import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response
import xelagurd.socialdating.client.data.AppLocaleManager

@Singleton
class LanguageInterceptor @Inject constructor(
    private val appLocaleManager: AppLocaleManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .header(ACCEPT_LANGUAGE, appLocaleManager.getRequestLanguage())
            .build()

        return chain.proceed(request)
    }

    private companion object {
        const val ACCEPT_LANGUAGE = "Accept-Language"
    }
}
