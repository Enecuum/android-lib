package com.enecuum.lib

import android.content.Context
import android.util.Log
import com.enecuum.lib.xor
import com.enecuum.lib.SageSign
import com.pixplicity.easyprefs.library.Prefs

object KeyStore {

    fun secretKey(context: Context, defaultValue: String = ""): String =
        SecurePrefs.getStringValue(context, SECRET_KEY, defaultValue) ?: defaultValue

    fun publicKey(context: Context, defaultValue: String = ""): String =
        SecurePrefs.getStringValue(context, PUBLIC_KEY, defaultValue) ?: defaultValue

    fun signKey(context: Context, defaultValue: String = ""): String =
        SecurePrefs.getStringValue(context, SIGN_KEY, defaultValue) ?: defaultValue

    fun userReferral(context: Context, defaultValue: String = ""): String =
        SecurePrefs.getStringValue(context, USER_REFERRAL, defaultValue) ?: defaultValue

    fun referrerCode(): String? {
        val referrerCode = Prefs.getString(REFERRER_CODE, null)
        if (referrerCode.isNullOrEmpty() || referrerCode == "null") {
            return null
        }
        return referrerCode
    }

    fun validateSecretKey(secretKey: String): Boolean {
        if (secretKey.isEmpty()) return false
        return secretKey.length == KEY_LENGTH || (secretKey.length >= KEY_LENGTH && secretKey.startsWith(
            KEY_PREFIX
        ))
    }

    fun saveKeys(context: Context, data: SageSign.GeneratedData) {
        SecurePrefs.setValue(context, SECRET_KEY, normalizeKey(data.secretKey))
        SecurePrefs.setValue(context, SIGN_KEY, data.signKey)
        SecurePrefs.setValue(context, PUBLIC_KEY, data.publicKey)
        SecurePrefs.setValue(context, USER_REFERRAL, publicKeyToUserReferral(data.publicKey))
    }

    fun saveKeys(context: Context, secretKey: String) {
        SecurePrefs.setValue(context, SECRET_KEY, normalizeKey(secretKey))
        val data = SageSign.retrievePublicKey(secretKey)
        SecurePrefs.setValue(context, SIGN_KEY, data.signKey)
        SecurePrefs.setValue(context, PUBLIC_KEY, data.publicKey)
        SecurePrefs.setValue(context, USER_REFERRAL, publicKeyToUserReferral(data.publicKey))
    }

    fun savePublicKey(context: Context, publicKey: String) {
        SecurePrefs.setValue(context, PUBLIC_KEY, publicKey)
        SecurePrefs.setValue(context, USER_REFERRAL, publicKeyToUserReferral(publicKey))
    }

    fun saveReferrerCode(code: String) {
        Prefs.putString(REFERRER_CODE, code)
    }

    fun resetAllKeys(context: Context) {
        SecurePrefs.clearAllValues(context)
    }

    fun referrerCodeToPublicKey(refKey: String) = refKey.substring(4) xor XOR_STRING

    fun publicKeyToUserReferral(publicKey: String) = REF_PREFIX + (publicKey xor XOR_STRING)

    fun migrateIfNeeded(context: Context) {
        val secretKey = Prefs.getString(SECRET_KEY, STUB)
        if (secretKey.isNullOrEmpty() || secretKey == STUB) {
            Log.d("KeyStore", "Migration not needed")
            return
        }
        saveKeys(context, secretKey)
        Prefs.putString(SECRET_KEY, STUB)
        Prefs.putString(PUBLIC_KEY, STUB)
        Prefs.putString(USER_REFERRAL, STUB)
        Prefs.putString(SIGN_KEY, STUB)
        Prefs.putString(UPLOAD_KEY, STUB)
    }

    private fun normalizeKey(secretKey: String): String {
        if (secretKey.length >= (KEY_LENGTH + KEY_PREFIX.length) && secretKey.startsWith(KEY_PREFIX)) {
            return secretKey.drop(KEY_PREFIX.length)
        }
        return secretKey
    }

    private const val KEY_LENGTH = 64
    private const val KEY_PREFIX = "00"
    private const val XOR_STRING =
        "750D7F2B34CA3DF1D6B7878DEBC8CF9A56BCB51A58435B5BCFB7E82EE09FA8BE75"
    private const val STUB = "null"

    private const val REF_PREFIX = "ref_"

    private const val SECRET_KEY = "secret_key"
    private const val PUBLIC_KEY = "public_key"
    private const val USER_REFERRAL = "public_ref_key"
    private const val SIGN_KEY = "sign_public_key"
    private const val UPLOAD_KEY = "upload_public_key"
    private const val REFERRER_CODE = "referral_public_key"
}