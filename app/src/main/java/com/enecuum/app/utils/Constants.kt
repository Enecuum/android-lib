package com.enecuum.app.utils

object Constants {

    //    const val URL_KEY = "url_key"
//    const val PROD_WS = "prod_ws"
//    const val WS_URL = "ws_url"
    const val IP = "ip"

    const val LANGUAGE_KEY = "language_key"

    const val BALANCE_KEY = "balance_key"
    const val SUM_BALANCE_KEY = "sum_balance_key"
    const val MINIMUM_STAKE_KEY = "minimum_stake_key"

    const val MESSAGE_BUNDLE_KEY = "message_bundle"

    var refuseReconnection = false

    const val DEEPLINK_ATTRIBUTE_SOURCE = "deeplink_attribute_source"
    const val DEEPLINK_ATTRIBUTE_CAMPAIGN = "deeplink_attribute_campaign"
    const val DEEPLINK_ATTRIBUTE_CONTENT = "deeplink_attributes_content"

    const val FIRST_MINING_RUN_LOGGED = "first_mining_run_logged"
    const val FIRST_MINING_TAG = "utm_source=tgdark&utm_content=mamont"

    const val MINING_TOKEN = "mining_token"
    const val MANAGEMENT_TOKEN = "management_token"

}

fun randomHexString(length: Int = 64): String {
    val allowedChars = "0123456789abcdef"
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}