package com.enecuum.lib.api

object TransactionCompat {

    data class Request(
        val amount: String,
        val from: String,
        val nonce: Long,
        val sign: String,
        val to: String,
        val data: String,
        val ticker: String
    )
}