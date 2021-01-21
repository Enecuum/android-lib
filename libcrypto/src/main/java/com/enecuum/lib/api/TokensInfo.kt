package com.enecuum.lib.api

data class TokensInfo(
    val data: Array<TokensInfoData>
)

data class TokensInfoData(
    val token_id: String,
    val website: String,
    val media: Object,
    val documents: Object,
    val description: Object,
    val coingecko_id: String
)