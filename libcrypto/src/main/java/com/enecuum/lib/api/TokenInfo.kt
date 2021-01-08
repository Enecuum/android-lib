package com.enecuum.lib.api

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
    val caption: String?,
    val active: Int,
    val reissuable: Int,
    val minable: Int,
    val max_supply: BigDecimal?,
    val block_reward: BigDecimal,
    val min_stake: BigDecimal,
    val referrer_stake: BigDecimal,
    val ref_share: BigDecimal
)