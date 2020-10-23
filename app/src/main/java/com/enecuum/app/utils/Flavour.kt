package com.enecuum.app.utils

import com.enecuum.app.BuildConfig

object Flavour {

    val isBit: Boolean
        get() = BuildConfig.FLAVOR == "bit"

    val isWallet: Boolean
        get() = BuildConfig.FLAVOR == "wallet"
}