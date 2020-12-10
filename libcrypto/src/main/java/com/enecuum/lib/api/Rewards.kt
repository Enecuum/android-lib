package com.enecuum.lib.api;

import java.math.BigDecimal
import java.math.BigInteger

data class Rewards(
    val balance: BigDecimal,
    val records: List<RewardItem>,
    val page_count: Int,
    val id: String
)

//TODO
data class RewardItem(
    val i: Int,
    val hash: String,
    val time: BigInteger,
    val rectype: String,
    val amount: BigDecimal,
    val irew: BigDecimal
)