package com.enecuum.app.data.api

data class Publish(
    val ver: Int,
    val method: String,
    val data: PublishData
)

data class PublishData(
    val kblocks_hash: String,
    val m_hash: String,
    val referrer: String?,
    val sign: String,
    val id: String,
    val token: String
)