package com.enecuum.lib.api

data class TransactionResponse(
    val err: Int,
    val result: List<TransactionResult>
)

data class TransactionResult(
    val hash: String,
    val status: Int
)