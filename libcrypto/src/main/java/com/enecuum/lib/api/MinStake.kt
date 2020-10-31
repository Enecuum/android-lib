package com.enecuum.lib.api

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class MinStake(@SerializedName("min_stake") val minStake: BigDecimal)