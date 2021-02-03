package com.enecuum.lib.api

//data class Coingecko(
//    val `enq-enecuum`: CoingeckoData,
//    val `nayuta-coin`: CoingeckoData,
//    val `bitcoin`: CoingeckoData,
//)

data class CoingeckoUSD(
    val usd: String
)

data class Probit(
    val data: Array<ProbitData>
)

data class ProbitData(
    val last: String,
    val low: String,
    val high: String,
    val change: String,
    val base_volume: String,
    val quote_volume: String,
    val market_id: String,
    val time: String
)