package com.enecuum.lib.api

import java.math.BigDecimal

data class ContractPrice(
    val create_token: BigDecimal,
    val delegate: BigDecimal,
    val undelegate: BigDecimal,
    val transfer: BigDecimal,
    val pos_reward: BigDecimal,
    val mint: BigDecimal,
    val burn: BigDecimal
)