package com.enecuum.app.api

import com.enecuum.app.data.api.*
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.http.*

interface Api {

    @GET
    fun getDetailedBalanceAsync(
        @Url url: String,
        @Query("id") id: String
    ): Deferred<DetailedBalance>

    @GET
    fun getTokensBalanceListAsync(
        @Url url: String,
        @Query("id") id: String
    ): Deferred<List<TokenBalance>>

    @GET
    fun getVersionAsync(@Url url: String): Deferred<Version>

    @GET
    fun getStatsAsync(@Url url: String): Deferred<Statistics>

    @POST
    fun postTransactionAsync(
        @Url url: String,
        @Body body: List<Transaction.Request>
    ): Deferred<TransactionResponse>

    @POST
    fun postTokenIssueAsync(
        @Url url: String,
        @Body body: List<TransactionCompat.Request>
    ): Deferred<TransactionResponse>

    @GET
    fun getTokenInfoAsync(@Url url: String, @Query("hash") hash: String): Deferred<List<TokenInfo>>

    @GET
    fun getReferrerStakeAsync(@Url url: String): Deferred<ReferrerStake>

    @GET
    fun getRoiAsync(@Url url: String): Deferred<List<Roi>>

    @GET
    fun getMinStakeAsync(@Url url: String): Deferred<MinStake>

    @GET
    fun getBlocksAsync(@Url url: String): Deferred<BlocksAmount>

    @GET
    fun getContractPriceAsync(@Url url: String): Deferred<ContractPrice>

    @GET
    fun getAllTickersAsync(@Url url: String): Deferred<List<Ticker>>

    @GET
    fun getPosListCountAsync(@Url url: String): Deferred<Count>

    @GET
    fun getPosListPageAsync(
        @Url url: String,
        @Query("page") pageNum: Long
    ): Deferred<PosContractPage>

    @GET
    fun getPosListAllAsync(@Url url: String): Deferred<PosContractPage>

    @GET
    fun getDelegatedListAsync(
        @Url url: String,
        @Query("delegator") publicKey: String
    ): Deferred<List<Validator>>

    @GET
    fun getUndelegatedListAsync(
        @Url url: String,
        @Query("delegator") publicKey: String
    ): Deferred<List<TransferData>>

    @GET
    fun getPosTotalActiveStakeAsync(@Url url: String): Deferred<TotalActiveStake>

    @GET
    fun getPosNamesAsync(@Url url: String): Deferred<List<PosName>>

    @POST
    fun get25BITAsync(
        @Url url: String,
        @Body body: Key
    ): Deferred<Boolean>
}