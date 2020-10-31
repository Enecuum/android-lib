package com.enecuum.lib.api

import java.math.BigDecimal

data class Validator(
    val pos_id: String,
    val owner: String,
    val rank: Int,
    val delegated: BigDecimal,
    val transit: BigDecimal,
    val undelegated: BigDecimal,
    val reward: BigDecimal?,
    val fee: Int
)