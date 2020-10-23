package com.enecuum.app.data.keys

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.enecuum.app.BuildConfig
import com.enecuum.app.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.*
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

object AndroidKeyStoreWrapper {

    private val keyStoreInstance: KeyStore
        @Throws(AndroidKeyStoreException::class)
        get() {
            try {
                return KeyStore.getInstance(KEY_KEYSTORE_NAME).apply {
                    // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
                    // to call "load", or it'll crash.
                    load(null)
                }
            } catch (e: Exception) {
                throw AndroidKeyStoreException(
                    e,
                    e.message,
                    AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
                )
            }
        }

    @Throws(AndroidKeyStoreException::class)
    fun encryptMessage(context: Context, plainMessage: String): String? {
        try {
            val input: Cipher = if (Build.VERSION.SDK_INT >= M) {
                Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER)
            } else {
                Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER)
            }
            input.init(Cipher.ENCRYPT_MODE, getPublicKey(context))

            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, input)
            cipherOutputStream.write(plainMessage.toByteArray(KEY_CHARSET))
            cipherOutputStream.close()

            val values = outputStream.toByteArray()
            return Base64.encodeToString(values, Base64.DEFAULT)

        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }
    }

    @Throws(AndroidKeyStoreException::class)
    fun decryptMessage(context: Context, encryptedMessage: String): String {
        try {
            val output: Cipher = if (Build.VERSION.SDK_INT >= M) {
                Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_MARSHMALLOW_PROVIDER)
            } else {
                Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLYBEAN_PROVIDER)
            }
            output.init(Cipher.DECRYPT_MODE, getPrivateKey(context))

            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(encryptedMessage, Base64.DEFAULT)), output
            )
            val values = ArrayList<Byte>()

            var nextByte: Int = cipherInputStream.read()
            while (nextByte != -1) {
                values.add(nextByte.toByte())
                nextByte = cipherInputStream.read()
            }

            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            return String(bytes, 0, bytes.size, KEY_CHARSET)
        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.CRYPTO_EXCEPTION
            )
        }
    }

    @Throws(AndroidKeyStoreException::class)
    fun keyPairExists(): Boolean {
        return try {
            keyStoreInstance.getKey(KEY_ALIAS, null) != null
        } catch (e: NoSuchAlgorithmException) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        } catch (e: KeyStoreException) {
            false
        } catch (e: UnrecoverableKeyException) {
            false
        }
    }

    @Throws(AndroidKeyStoreException::class)
    fun generateKeyPair(context: Context) {
        if (!keyPairExists()) {
            if (Build.VERSION.SDK_INT >= M) {
                generateKeyPairForMarshmallow(context)
            } else {
                PRNGFixes.apply()
                generateKeyPairUnderMarshmallow(context)
            }
        } else if (BuildConfig.DEBUG) {
            Log.e(
                "AndroidKeyStoreWrapper",
                context.getString(R.string.message_keypair_already_exists)
            )
        }
    }

    @Throws(AndroidKeyStoreException::class)
    fun deleteKeyPair(context: Context) {
        // Delete Key from Keystore
        if (keyPairExists()) {
            try {
                keyStoreInstance.deleteEntry(KEY_ALIAS)
            } catch (e: KeyStoreException) {
                throw AndroidKeyStoreException(
                    e,
                    e.message,
                    AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
                )
            }

        } else if (BuildConfig.DEBUG) {
            Log.e(
                "AndroidKeyStoreWrapper",
                context.getString(R.string.message_keypair_does_not_exist)
            )
        }
    }

    @Throws(AndroidKeyStoreException::class)
    private fun getPublicKey(context: Context): PublicKey? {
        val publicKey: PublicKey
        try {
            if (keyPairExists()) {
                publicKey = keyStoreInstance.getCertificate(KEY_ALIAS).publicKey
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e(
                        "AndroidKeyStoreWrapper",
                        context.getString(R.string.message_keypair_does_not_exist)
                    )
                }
                throw AndroidKeyStoreException(
                    null,
                    context.getString(R.string.message_keypair_does_not_exist),
                    AndroidKeyStoreException.ExceptionType.WEIRD_INTERNAL_EXCEPTION
                )
            }
        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }

        return publicKey
    }

    @Throws(AndroidKeyStoreException::class)
    private fun getPrivateKey(context: Context): PrivateKey? {
        val privateKey: PrivateKey
        try {
            if (keyPairExists()) {
                privateKey = keyStoreInstance.getKey(KEY_ALIAS, null) as PrivateKey
            } else {
                if (BuildConfig.DEBUG) {
                    Log.e(
                        "AndroidKeyStoreWrapper",
                        context.getString(R.string.message_keypair_does_not_exist)
                    )
                }
                throw AndroidKeyStoreException(
                    null,
                    context.getString(R.string.message_keypair_does_not_exist),
                    AndroidKeyStoreException.ExceptionType.WEIRD_INTERNAL_EXCEPTION
                )
            }
        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }

        return privateKey
    }

    private fun isRTL(context: Context): Boolean {
        val config = context.resources.configuration
        return config.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    @RequiresApi(api = M)
    @Throws(AndroidKeyStoreException::class)
    private fun generateKeyPairForMarshmallow(context: Context) {
        try {
            if (isRTL(context)) {
                Locale.setDefault(Locale.US)
            }

            val generator =
                KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME)

            val keyGenParameterSpec =
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()

            generator.initialize(keyGenParameterSpec)
            generator.generateKeyPair()
        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }

    }

    @Throws(AndroidKeyStoreException::class)
    private fun generateKeyPairUnderMarshmallow(context: Context) {
        try {
            if (isRTL(context)) {
                Locale.setDefault(Locale.US)
            }

            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 99)

            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(KEY_ALIAS)
                .setSubject(X500Principal(KEY_X500PRINCIPAL))
                .setSerialNumber(BigInteger.TEN)
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()

            val generator =
                KeyPairGenerator.getInstance(KEY_ENCRYPTION_ALGORITHM, KEY_KEYSTORE_NAME)
            generator.initialize(spec)
            generator.generateKeyPair()
        } catch (e: Exception) {
            throw AndroidKeyStoreException(
                e,
                e.message,
                AndroidKeyStoreException.ExceptionType.KEYSTORE_EXCEPTION
            )
        }

    }

    private val KEY_CHARSET = Charsets.UTF_8
    private const val KEY_ALIAS = "enqKeyPair"
    private const val KEY_ENCRYPTION_ALGORITHM = "RSA"
    private const val KEY_KEYSTORE_NAME = "AndroidKeyStore"
    private const val KEY_CIPHER_JELLYBEAN_PROVIDER = "AndroidOpenSSL"
    private const val KEY_CIPHER_MARSHMALLOW_PROVIDER = "AndroidKeyStoreBCWorkaround"
    private const val KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private const val KEY_X500PRINCIPAL = "CN=Enecuum HK Limited"
}