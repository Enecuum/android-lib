package com.enecuum.lib.api.main

import com.enecuum.lib.BuildConfig

object ApiRouter {

    enum class Route(private val path: String) {
        DETAILED_BALANCE("balance"),
        BALANCE_ALL("balance_all"),
        BALANCE_MINABLE("balance_minable"),
        TRANSACTION("tx"),
        STATS("stats"),
        BLOCKS("height"),
        REFERRER_STAKE("referrer_stake"),
        ROI("roi"),
        MIN_STAKE("get_min_stake"),
        VER("ver"),
        TOKEN_INFO("token_info"),
        TICKERS_ALL("get_tickers_all"),
        CONTRACT_PRICE("contract_pricelist"),
        POS_LIST_COUNT("get_pos_list_count"),
        POS_LIST_PAGE("get_pos_list_page"),
        POS_LIST_ALL("get_pos_list_all"),
        DELEGATED_LIST("get_delegated_list"),
        UNDELEGATED_LIST("get_undelegated_list"),
        POS_TOTAL_ACTIVE_STAKE("get_pos_active_total_stake"),
        POS_NAMES("get_pos_names");

        val url: String
            get() = "$apiURL$path"
    }

    private val baseIP: String by lazy {

        return@lazy when {
            BuildConfig.DEBUG -> debugIp
            else -> prodIp
        }
    }

    val apiURL: String by lazy {
        return@lazy "$httpProtocolPrefix$baseIP:$httpProtocolPort$apiSuffix"
    }

    val wsURL: String by lazy {
        return@lazy "$wsProtocolPrefix$baseIP:$wsProtocolPort"
    }

    //TODO SSL/TLS please
    var wsProtocolPrefix = "ws://"
    var httpProtocolPrefix = "http://"

    var wsProtocolPort = BuildConfig.WS_PROTOCOL_PORT
    var httpProtocolPort = BuildConfig.HTTP_PROTOCOL_PORT
    var apiSuffix = BuildConfig.API_SUFFIX
    var debugIp = BuildConfig.DEBUG_IP
    var prodIp = BuildConfig.PROD_IP
}