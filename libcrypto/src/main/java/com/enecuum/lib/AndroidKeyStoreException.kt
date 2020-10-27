package com.enecuum.lib

class AndroidKeyStoreException(
    cause: Throwable?,
    message: String?,
    val type: ExceptionType
) : Exception(message, cause) {

    enum class ExceptionType {
        KEYSTORE_EXCEPTION,
        CRYPTO_EXCEPTION,
        KEYSTORE_NOT_SUPPORTED_EXCEPTION,
        WEIRD_INTERNAL_EXCEPTION
    }
}