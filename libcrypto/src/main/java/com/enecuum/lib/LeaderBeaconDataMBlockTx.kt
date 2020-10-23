package com.enecuum.lib

data class LeaderBeaconDataMBlockTx(
    val from: String,
    val to: String,
    val amount: Long,
    val nonce: Long,
    val sign: String,
    val data: String,
    val ticker: String
)