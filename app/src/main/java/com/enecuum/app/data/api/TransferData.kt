package com.enecuum.app.data.api

import java.math.BigDecimal

data class TransferData(
    val tx_hash: String,
    val pos_id: String,
    val amount: BigDecimal,
    val transfer_lock: Int
)