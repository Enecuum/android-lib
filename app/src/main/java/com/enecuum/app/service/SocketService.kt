package com.enecuum.app.service

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.enecuum.app.Config.SOCKET_RECONNECT_TIMEOUT_IN_MILLIS
import com.enecuum.app.api.ApiRouter
import com.enecuum.app.data.api.Hail
import com.enecuum.app.data.api.HailData
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.utils.Constants
import com.enecuum.app.utils.Constants.MESSAGE_BUNDLE_KEY
import com.enecuum.app.utils.SageSign
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.net.URI

class SocketService(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) : LooperCallback {

    private val looper by lazy { MessageLooper(context, this) }
    private val gson = Gson()

    private var mutableStatus: MutableLiveData<ServiceStatus> = MutableLiveData()
    val status: LiveData<ServiceStatus> = mutableStatus

    private lateinit var okSocket: WebSocket
    private val okSocketListener by lazy {
        object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket?, response: Response?) {
                Log.d("SocketService", "Websocket opened")

                mutableStatus.postValue(ServiceStatus.CONNECTING)
                val hailData = HailData(
                    SageSign.getSha256(URI(url).host),
                    SageSign.sign(context, KeyStore.secretKey(context).toByteArray()),
                    KeyStore.publicKey(context)
                )
                sendMessage(gson.toJson(Hail(data = hailData)))

                mutableStatus.postValue(ServiceStatus.STARTED)

//                reconnectTimeout = SOCKET_RECONNECT_TIMEOUT_IN_MILLIS
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val messageObject = JSONObject(text)
                if (messageObject.has(ERROR_FIELD)) {
                    val errorValue = messageObject.getString(ERROR_FIELD)
                    if (errorValue == ServiceError.ERR_WRONG_PROTOCOL_VERSION.name) {
                        stopServiceWithError(ServiceError.ERR_WRONG_PROTOCOL_VERSION)
                        return
                    }
                }

//                errorReconnectTs = 0L

                val message = Message()
                message.data = Bundle().apply { putString(MESSAGE_BUNDLE_KEY, text) }
                looper.handler.sendMessage(message)
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
                Log.d("SocketService", "Websocket closed with error")
                if (!Constants.refuseReconnection) {
                    reconnectWithDelay()
                } else {
                    Constants.refuseReconnection = false
                }
            }
        }
    }

    private var url = ApiRouter.wsURL
//    private var errorReconnectTs: Long = 0

    fun startService(url: String) {
        this.url = url
        val request = Request.Builder().url(url).build()
        okSocket = okHttpClient.newWebSocket(request, okSocketListener)
        if (!looper.isAlive) looper.start()
    }

    fun stopService() {
        okSocket.close(1000, null)
        mutableStatus.postValue(ServiceStatus.STOPPED)
//        reconnectTimeout = SOCKET_RECONNECT_TIMEOUT_IN_MILLIS
        Log.d("SocketService", "Websocket stopped")
    }

    private fun reconnectWithDelay() {
        mutableStatus.postValue(ServiceStatus.CONNECTING)
        CoroutineScope(Dispatchers.Default).launch {
            Log.d("SocketService", "Reconnecting in $reconnectTimeout sec")
            delay(reconnectTimeout)
//            reconnectTimeout *= 2
            restartService(url)
        }
    }

    override fun restartService(url: String) {
        okSocket.close(1000, null)
        startService(url)
    }

    override fun sendMessage(message: String) {
        okSocket.send(message)
    }

    override fun stopServiceWithError(error: ServiceError) {
        Log.d("SocketService", "Got server error ${error.name}")

/*
        if (error == ServiceError.ERR_DUPLICATE_KEY) {
            // entering this case for first time, saving timestamp
            if (errorReconnectTs == 0L) {
                errorReconnectTs = System.currentTimeMillis()
            }

            val reconnectionTime = System.currentTimeMillis() - errorReconnectTs
            if (reconnectionTime < MAX_ERROR_RECONNECT_TIME_IN_MS) {
                Constants.refuseReconnection = false

                Log.d(
                    "SocketService",
                    "Ignoring dup key error for ${MAX_ERROR_RECONNECT_TIME_IN_MS - reconnectionTime} ms to cover fast reconnection false negative"
                )
                return
            } else {
                errorReconnectTs = 0L
            }
        }
*/

        Constants.refuseReconnection = true
        okSocket.close(1000, null)

        val status: ServiceStatus = when (error) {
            ServiceError.ERR_WRONG_PROTOCOL_VERSION -> ServiceStatus.TERMINATED_PROTO_ERROR
//            ServiceError.ERR_DUPLICATE_KEY -> ServiceStatus.TERMINATED_DUP_KEY
        }
        mutableStatus.postValue(status)
    }

    companion object {
        //        private const val MAX_ERROR_RECONNECT_TIME_IN_MS = 60 * 1000
        private const val ERROR_FIELD = "err"
        private const val reconnectTimeout = SOCKET_RECONNECT_TIMEOUT_IN_MILLIS
    }
}

enum class ServiceError { ERR_WRONG_PROTOCOL_VERSION /*, ERR_DUPLICATE_KEY*/ }