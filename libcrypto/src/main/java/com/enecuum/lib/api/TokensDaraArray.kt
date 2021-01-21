package com.enecuum.lib.api

data class TokensDaraArray(
    val data: Array<TokensDara>
)

data class TokensDara(
    val token_id: String,
    val website: String,
//    val media: Object,
//    val documents: Object,
//    val description: Object,
    val coingecko_id: String
)