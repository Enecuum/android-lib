package com.enecuum.app.utils

import android.content.res.Resources
import com.enecuum.app.BuildConfig
import com.enecuum.app.R

object BalanceStringFormatter {

    fun balanceString(resources: Resources, balance: String, ticker: String): String =
        resources.getString(R.string.balance_enq, balance, ticker)

    fun noCurrencyString(resources: Resources): String =
        resources.getString(R.string.no_enq, BuildConfig.TICKER)
}