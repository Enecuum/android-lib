package com.enecuum.lib.api

data class LeaderBeacon(
    val ver: Int,
    val method: String,
    val data: LeaderBeaconData
)

data class LeaderBeaconData(
    val leader_id: Long,
    val m_hash: String,
    val leader_sign: LeaderBeaconDataSign,
    val mblock_data: LeaderBeaconDataMBlock
)

data class LeaderBeaconDataSign(
    val r: LeaderBeaconDataSignPoint,
    val s: LeaderBeaconDataSignPoint
)

data class LeaderBeaconDataSignPoint(
    val x: List<String>,
    val y: List<String>
)

data class LeaderBeaconDataMBlock(
    val kblocks_hash: String,
    val publisher: String,
    val nonce: Long,
    val txs: List<LeaderBeaconDataMBlockTx>
)