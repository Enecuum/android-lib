package com.enecuum.app.data.api

import java.math.BigDecimal

data class TokenInfo(
    val hash: String,
    val owner: String,
    val fee_type: Int,
    val fee_value: BigDecimal,
    val fee_min: BigDecimal?,
    val ticker: String,
    val decimals: Int,
    val total_supply: BigDecimal?,
    val name: String?,
    val minable: Int,
    val reissuable: Int,
    val min_stake: BigDecimal
)