package com.enecuum.lib.api

import com.enecuum.lib.SageSign

object Transaction {

    fun constructHash(
        amount: String,
        nonce: String,
        from: String,
        to: String,
        data: String,
        ticker: String
    ): String {
        val str =
            "${SageSign.getSha256(amount)}${SageSign.getSha256(data)}${SageSign.getSha256(from)}${
                SageSign.getSha256(nonce)
            }${SageSign.getSha256(ticker)}${SageSign.getSha256(to)}"
        return SageSign.getSha256(str)
    }

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
