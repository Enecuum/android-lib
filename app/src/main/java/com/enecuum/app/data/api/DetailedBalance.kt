package com.enecuum.app.data.api

import java.math.BigDecimal

data class DetailedBalance(
    val amount: BigDecimal = BigDecimal.ZERO,
    val delegated: BigDecimal = BigDecimal.ZERO,
    val transit: BigDecimal = BigDecimal.ZERO,
    val undelegated: BigDecimal = BigDecimal.ZERO,
    val reward: BigDecimal = BigDecimal.ZERO
)

fun DetailedBalance.sumBalance(): BigDecimal =
    amount.plus(delegated).plus(reward).plus(transit).plus(undelegated)