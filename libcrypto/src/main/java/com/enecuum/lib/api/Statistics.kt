package com.enecuum.lib.api

import java.math.BigDecimal

data class Statistics(
    val hashrate: String?,
    val accounts: Int,
    val csup: BigDecimal,
    val reward_poa: BigDecimal?,
    val reward_pow: BigDecimal?,
    val reward_pos: BigDecimal?,
    val pos_count: Int?,
    val pow_count: Int?,
    val poa_count: Int?,
    val tps: Int?,
    val max_tps: Int?,
    val cg_usd: BigDecimal?,
    val total_daily_stake: BigDecimal,
    val difficulty: String
)