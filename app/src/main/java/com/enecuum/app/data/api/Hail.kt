package com.enecuum.app.data.api

import com.enecuum.app.BuildConfig

data class Hail(
    val ver: Int = BuildConfig.TRINITY_VERSION,
    val method: String = "hail",
    val data: HailData
)

data class HailData(
    val hash: String,
    val sign: String,
    val id: String
)