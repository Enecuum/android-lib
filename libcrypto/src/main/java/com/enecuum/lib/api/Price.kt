package com.enecuum.lib.api

data class Coingecko(
    val `enq-enecuum`: CoingeckoData
)

data class CoingeckoData(
    val usd: String
)