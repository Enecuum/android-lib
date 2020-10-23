package com.enecuum.app.data.keys

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.TextUtils
import com.enecuum.app.R

object SecurePrefs {

    @Throws(AndroidKeyStoreException::class)
    fun setValue(context: Context, key: String, value: String) {
        val applicationContext = context.applicationContext
        if (!AndroidKeyStoreWrapper.keyPairExists()) {
            AndroidKeyStoreWrapper.generateKeyPair(applicationContext)
        }

        val transformedValue = AndroidKeyStoreWrapper.encryptMessage(applicationContext, value)
        if (TextUtils.isEmpty(transformedValue)) {
            throw AndroidKeyStoreException(
                null,
                context.getString(R.string.message_problem_encryption),
                AndroidKeyStoreException.ExceptionType.CRYPTO_EXCEPTION
            )
        } else {
            setSecureValue(applicationContext, key, transformedValue!!)
        }
    }

    fun getStringValue(context: Context, key: String, defValue: String?): String? {
        val applicationContext = context.applicationContext
        val result = getSecureValue(applicationContext, key)
        return try {
            if (!TextUtils.isEmpty(result)) {
                AndroidKeyStoreWrapper.decryptMessage(applicationContext, result!!)
            } else {
                defValue
            }
        } catch (e: AndroidKeyStoreException) {
            defValue
        }
    }

    @Throws(AndroidKeyStoreException::class)
    fun clearAllValues(context: Context) {
        val applicationContext = context.applicationContext
        if (AndroidKeyStoreWrapper.keyPairExists()) {
            AndroidKeyStoreWrapper.deleteKeyPair(applicationContext)
        }
        clearAllSecureValues(applicationContext)
    }

    private fun setSecureValue(context: Context, key: String, value: String) {
        val preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        preferences.edit().putString(key, value).apply()
    }

    private fun getSecureValue(context: Context, key: String): String? {
        val preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        return preferences.getString(key, null)
    }

    private fun clearAllSecureValues(context: Context) {
        val preferences = context.getSharedPreferences(KEY_SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        preferences.edit().clear().apply()
    }

    private const val KEY_SHARED_PREFERENCES_NAME = "EnqPrefs"
}