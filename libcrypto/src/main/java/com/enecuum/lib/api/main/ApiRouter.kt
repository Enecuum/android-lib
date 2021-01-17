package com.enecuum.lib.api.main

import com.enecuum.lib.BuildConfig

object ApiRouter {

    var setter: ConnectionSetter

    init {
       setter = getConnectionSetter(false)
    }

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

    //TODO SSL/TLS please
    val httpProtocolPrefix = "https://"
    val wsProtocolPrefix = "ws://"

    val wsURL : String
        get() = wsProtocolPrefix + setter.ip + ":" + setter.portWs

    val apiURL : String
        get() = httpProtocolPrefix + setter.domain + ":" + setter.portHttp + setter.apiSuffix

    val mpkx : String
        get() = setter.mpkx
    val mpky : String
        get() = setter.mpky

    class ConnectionSetter(
        var ip: String,
        var domain: String,
        var portWs: Int,
        var portHttp: Int,
        var apiSuffix: String,
        var mpkx: String,
        var mpky: String
    )

    fun getConnectionSetter(useDebug: Boolean): ConnectionSetter {
        if (useDebug) {
            return ConnectionSetter(
                BuildConfig.DEBUG_IP,
                BuildConfig.DEBUG_DOMAIN,
                BuildConfig.WS_PROTOCOL_PORT,
                BuildConfig.HTTP_PROTOCOL_PORT,
                BuildConfig.API_SUFFIX,
                BuildConfig.debug_mpkx,
                BuildConfig.debug_mpky
            )
        } else {
            return ConnectionSetter(
                BuildConfig.PROD_IP,
                BuildConfig.PROD_DOMAIN,
                BuildConfig.WS_PROTOCOL_PORT,
                BuildConfig.HTTP_PROTOCOL_PORT,
                BuildConfig.API_SUFFIX,
                BuildConfig.prod_mpkx,
                BuildConfig.prod_mpky
            )
        }
    }
}