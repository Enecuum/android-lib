package com.enecuum.app

import android.content.Context
import android.content.ContextWrapper
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.enecuum.app.di.networkModule
import com.enecuum.app.di.appModule
import com.pixplicity.easyprefs.library.Prefs
import org.koin.android.ext.android.startKoin

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule, networkModule))

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}