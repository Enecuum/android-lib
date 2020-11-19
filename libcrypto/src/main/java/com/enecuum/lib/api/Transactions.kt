package com.enecuum.lib.api;

import java.math.BigDecimal
import java.math.BigInteger

data class Transactions(
    val balance: BigDecimal,
    val records: List<TransactionItem>,
    val page_count: Int,
    val id: String
)

//TODO
data class TransactionItem(
    val i: Int,
    val hash: String,
    val time: BigInteger,
    val rectype: String,
    val amount: BigDecimal,
    val data: String,
    val status: Int,
    val token_hash: String,
    val fee_type: Int,
    val fee_value: BigDecimal,
    val fee_min: BigDecimal
)