package com.enecuum.lib.api

data class Version(
    val minApkVersion: String,
    val maxApkVersion: String,
    val apkUrl: String
)