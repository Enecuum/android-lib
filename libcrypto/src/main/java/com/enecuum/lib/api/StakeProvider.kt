package com.enecuum.lib.api

import java.math.BigDecimal

data class StakeProvider(
    val pos_id: String,
    val owner: String,
//    val stake_power: Double,
    val rank: Int,
    val stake: BigDecimal,
    val reward: BigDecimal?,
    val fee: Int,
    val roi: BigDecimal?,
    val active: Int,
    val uptime: Double,
    val active_stake_share: Double,
    val active_stake_power: Double
)