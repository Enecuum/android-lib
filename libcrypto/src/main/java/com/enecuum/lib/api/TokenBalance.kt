package com.enecuum.lib.api

data class TokenBalance(
    val amount: String,
    val token: String,
    val ticker: String,
    val decimals: Int,
    val minable: Int,
    val reissuable: Int
)