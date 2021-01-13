package com.enecuum.lib.api.main

import com.enecuum.lib.BuildConfig

object ApiRouter {

    enum class Route(private val path: String) {
        DETAILED_BALANCE("balance"),
        BALANCE_ALL("balance_all"),
        ACCOUNT_TRANSACTIONS("account_transactions"),
        ACCOUNT_REWARDS("account_rewards"),
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

    private val baseHost: String by lazy {

        return@lazy when {
            BuildConfig.DEBUG -> debugHost
            else -> prodHost
        }
    }

    val apiURL: String by lazy {
        return@lazy "$httpProtocolPrefix$baseHost:$httpsProtocolPort$apiSuffix"
    }

    val wsURL: String by lazy {
        return@lazy "$wsProtocolPrefix$baseHost:$wsProtocolPort"
    }

    val mpkx: String by lazy {
        return@lazy when {
            BuildConfig.DEBUG -> BuildConfig.debug_mpkx
            else -> BuildConfig.prod_mpkx
        }
    }

    val mpky: String by lazy {
        return@lazy when {
            BuildConfig.DEBUG -> BuildConfig.debug_mpky
            else -> BuildConfig.prod_mpky
        }
    }

    //TODO SSL/TLS please
    var wsProtocolPrefix = "ws://"
    var httpProtocolPrefix = "https://"

    var wsProtocolPort = BuildConfig.WS_PROTOCOL_PORT
    var httpProtocolPort = BuildConfig.HTTP_PROTOCOL_PORT
    var httpsProtocolPort = BuildConfig.HTTPS_PROTOCOL_PORT
    var apiSuffix = BuildConfig.API_SUFFIX
    var debugHost = BuildConfig.DEBUG_DOMAIN
    var prodHost = BuildConfig.PROD_DOMAIN

    var coingecko = BuildConfig.COINGECKO
    var probit = BuildConfig.PROBIT
}