package com.enecuum.app.data.api

data class Peer(
    val ver: Int,
    val method: String,
    val data: PeerData
)

data class PeerData(
    val ip: String,
    val port: Int
)