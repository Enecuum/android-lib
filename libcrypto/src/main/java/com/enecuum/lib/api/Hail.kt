package com.enecuum.lib.api

import com.enecuum.lib.BuildConfig

data class Hail(
    val ver: Int = BuildConfig.TRINITY_VERSION,
    val method: String = "hail",
    val data: HailData
)

data class HailData(
    val hash: String,
    val sign: String,
    val token: String,
    val id: String
)