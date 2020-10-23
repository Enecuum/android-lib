package com.enecuum.app.data.api

data class TransactionResponse(
    val err: Int,
    val result: List<TransactionResult>
)

data class TransactionResult(
    val hash: String,
    val status: Int
)