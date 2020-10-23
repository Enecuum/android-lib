package com.enecuum.app.service

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.enecuum.app.BuildConfig
import com.enecuum.app.data.api.LeaderBeacon
import com.enecuum.app.data.api.Peer
import com.enecuum.app.data.api.Publish
import com.enecuum.app.data.api.PublishData
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.utils.Constants
import com.enecuum.app.utils.Constants.MESSAGE_BUNDLE_KEY
import com.enecuum.app.utils.SageSign
import com.enecuum.bit.crypto.LibCryptoMobile
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONException
import org.json.JSONObject


class MessageLooper(
    private val context: Context,
    private val callback: LooperCallback
) : Thread() {

    lateinit var handler: Handler
    private val gson = Gson()

    override fun run() {
        Looper.prepare()
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                val rawMessage = msg.data.getString(MESSAGE_BUNDLE_KEY)
                try {
                    val objectMessage = JSONObject(rawMessage)
                    when (objectMessage.getString(METHOD_FIELD)) {
                        Method.PEER.method -> {
                            val peer = gson.fromJson(rawMessage, Peer::class.java)
                            callback.restartService("ws://${peer.data.ip}:${peer.data.port}")
                        }
                        Method.BEACON.method -> {
                            val beacon = gson.fromJson(rawMessage, LeaderBeacon::class.java)

                            Log.d(
                                "SocketService",
                                "Websocket msg received: BEACON leader id ${beacon.data.leader_id}"
                            )

                            var leaderSignVerification = false

                            if (!BuildConfig.SKIP_VERIFICATION) try {

                                leaderSignVerification = LibCryptoMobile().verifyMobileCompat(
                                    beacon.data.mblock_data.kblocks_hash,
                                    beacon.data.m_hash,
                                    beacon.data.leader_id.toString(),
                                    beacon.data.mblock_data.nonce,
                                    beacon.data.leader_sign.r.x,
                                    beacon.data.leader_sign.r.y,
                                    beacon.data.leader_sign.s.x,
                                    beacon.data.leader_sign.s.y
                                )

                            } catch (e: Exception) {
                            }

                            if (leaderSignVerification || BuildConfig.SKIP_VERIFICATION) {

                                Log.d("SocketService", "Sign verified")
                                val txsHash = SageSign.constructTxsHash(beacon.data.mblock_data.txs)
                                val mHash =
                                    SageSign.getSha256(beacon.data.mblock_data.kblocks_hash + beacon.data.mblock_data.nonce.toString() + beacon.data.mblock_data.publisher + txsHash)

                                Log.d("SocketService", "SHA_HASH $mHash")
                                Log.d("SocketService", "CONTROL_SHA_HASH ${beacon.data.m_hash}")
                                if (mHash == beacon.data.m_hash) {
                                    val referrerCode: String? = KeyStore.referrerCode()
                                    val referrerPublicKey: String? =
                                        if (referrerCode.isNullOrEmpty()) null
                                        else KeyStore.referrerCodeToPublicKey(referrerCode)

                                    //TODO
                                    val token = Prefs.getString(Constants.MINING_TOKEN, "")
                                    Log.d("MainFragment", "MINING TOKEN")
                                    Log.d("MainFragment", token)

                                    val signM = SageSign.sign(
                                        context,
                                        (beacon.data.m_hash + (referrerPublicKey
                                            ?: "") + token).toByteArray(Charsets.UTF_8)
                                    )
                                    val publishData = PublishData(
                                        beacon.data.mblock_data.kblocks_hash,
                                        beacon.data.m_hash,
                                        referrerPublicKey,
                                        signM,
                                        KeyStore.publicKey(context),
                                        token
                                    )
                                    val publish =
                                        Publish(
                                            BuildConfig.TRINITY_VERSION,
                                            Method.PUBLISH.method,
                                            publishData
                                        )

                                    Log.d(
                                        "SocketService",
                                        "Websocket msg sent: PUBLISH sign $signM"
                                    )

                                    callback.sendMessage(gson.toJson(publish))
                                }
                            }
                        }
                        else -> {
                            Crashlytics.logException(JSONException("SocketService: ERROR IN MESSAGE\n$rawMessage"))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Crashlytics.logException(JSONException("SocketService: ERROR IN MESSAGE\n$rawMessage\n${e.stackTrace?.contentToString()}"))
                }
            }
        }

        Looper.loop()
    }

    companion object {
        const val METHOD_FIELD = "method"
    }
}

enum class Method(val method: String) {
    PEER("peer"),
    BEACON("on_leader_beacon"),
    PUBLISH("publish")
}