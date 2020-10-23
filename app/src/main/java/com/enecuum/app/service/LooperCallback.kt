package com.enecuum.app.service

interface LooperCallback {
    fun restartService(url: String)
    fun sendMessage(message: String)
    fun stopServiceWithError(error: ServiceError)
}