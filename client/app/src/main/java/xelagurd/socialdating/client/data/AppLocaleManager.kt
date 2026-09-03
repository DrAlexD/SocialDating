package xelagurd.socialdating.client.data

import javax.inject.Inject
import javax.inject.Singleton
import android.app.LocaleManager
import android.os.LocaleList
import xelagurd.socialdating.client.data.model.enums.AppLanguage

@Singleton
class AppLocaleManager @Inject constructor(
    private val localeManager: LocaleManager
) {

    fun getAppLanguage() = AppLanguage.fromLanguageTag(localeManager.applicationLocales[0]?.language)

    fun setAppLanguage(appLanguage: AppLanguage) {
        localeManager.applicationLocales = if (appLanguage == AppLanguage.SYSTEM) {
            LocaleList.getEmptyLocaleList()
        } else {
            LocaleList.forLanguageTags(appLanguage.languageTag)
        }
    }
}
