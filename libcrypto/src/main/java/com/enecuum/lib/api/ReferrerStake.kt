package com.enecuum.lib.api

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ReferrerStake(@SerializedName("referrer_stake") val referrerStake: BigDecimal)