package com.enecuum.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.enecuum.app.R
import com.enecuum.app.utils.Constants.LANGUAGE_KEY
import com.pixplicity.easyprefs.library.Prefs
import java.util.*

object LocaleHelper {

    fun initLocale(context: Context) {
        val languages = context.resources.getStringArray(R.array.language_code)
        val language = if (Prefs.getString(LANGUAGE_KEY, "") == "") {
            if (languages.contains(Locale.getDefault().language)) Locale.getDefault().language else "en"
        } else {
            Prefs.getString(LANGUAGE_KEY, "")
        }
        setLocale(context, language)
    }

    fun setLocale(context: Context, language: String) {
        Prefs.putString(LANGUAGE_KEY, language)
        update(context, Locale(language, "", ""))
    }

    fun getLocale(): String {
        return Prefs.getString(LANGUAGE_KEY, "")
    }

    private fun update(context: Context, locale: Locale) {
        updateResources(context, locale)
        val appContext = context.applicationContext
        if (appContext !== context) {
            updateResources(appContext, locale)
        }
    }

    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)

        val res = context.resources
        val current = res.configuration.getLocaleCompat()

        if (current == locale) return

        val config = Configuration(res.configuration)
        when {
            isAtLeastSdkVersion(Build.VERSION_CODES.N) -> setLocaleForApi24(config, locale)
            isAtLeastSdkVersion(Build.VERSION_CODES.JELLY_BEAN_MR1) -> config.setLocale(locale)
            else -> config.locale = locale
        }
        res.updateConfiguration(config, res.displayMetrics)
    }

    private fun isAtLeastSdkVersion(versionCode: Int): Boolean {
        return Build.VERSION.SDK_INT >= versionCode
    }

    @SuppressLint("NewApi")
    private fun setLocaleForApi24(config: Configuration, locale: Locale) {
        // bring the target locale to the front of the list
        val set = linkedSetOf(locale)

        val defaultLocales = LocaleList.getDefault()
        val all = List<Locale>(defaultLocales.size()) { defaultLocales[it] }
        // append other locales supported by the user
        set.addAll(all)

        //TODO
        config.locale = locale
    }

    @Suppress("DEPRECATION")
    private fun Configuration.getLocaleCompat(): Locale {
        return if (isAtLeastSdkVersion(Build.VERSION_CODES.N)) locales.get(0) else locale
    }
}