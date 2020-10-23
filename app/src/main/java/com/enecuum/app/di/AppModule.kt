package com.enecuum.app.di

//import com.enecuum.app.vvm.balance.DetailedBalanceViewModel
//import com.enecuum.app.vvm.balance.TokenBalanceViewModel
//import com.enecuum.app.vvm.common.BalanceViewModel
//import com.enecuum.app.vvm.main.MainViewModel
//import com.enecuum.app.vvm.referral.ReferralViewModel
//import com.enecuum.app.vvm.roi.RoiViewModel
//import com.enecuum.app.vvm.statistic.StatisticViewModel
//import com.enecuum.app.vvm.token.issue.TokenIssueViewModel
//import com.enecuum.app.vvm.token.management.TokenMintBurnViewModel
//import com.enecuum.app.vvm.token.smartstaking.*
//import com.enecuum.app.vvm.token.smartstaking.transferback.ValidatorViewModel
//import com.enecuum.app.vvm.token.transfer.TransferViewModel
//import com.enecuum.app.vvm.token.TransactionViewModel
import com.enecuum.app.data.livedata.*
import com.enecuum.app.vvm.common.BalanceViewModel
import com.enecuum.app.vvm.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    single<DetailedBalanceLiveDataRepository> { DetailedBalanceLiveData() }
    single<TokensBalanceLiveDataRepository> { TokensBalanceLiveData() }
    single<TransactionLiveDataRepository> { TransactionLiveData() }
    single<StatisticLiveDataRepository> { StatisticLiveData() }
    single<RoiLiveDataRepository> { RoiLiveData() }
    single<TickersLiveDataRepository> { TickersLiveData() }
    single<StakeProvidersLiveDataRepository> { StakeProvidersLiveData() }
    single<ValidatorsLiveDataRepository> { ValidatorsLiveData() }
    single<PosNamesLiveDataRepository> { PosNamesLiveData() }

//    single { StakeProviderDataFactory(get(), get()) }

//    viewModel { MainViewModel(androidContext(), get(), get(), get()) }
    viewModel { HomeViewModel(androidContext(), get(), get(), get()) }
    viewModel { BalanceViewModel(androidContext(), get(), get(), get()) }
//    viewModel { StatisticViewModel(androidContext(), get(), get(), get(), get()) }
//    viewModel { TransferViewModel(androidContext(), get(), get(), get()) }
//    viewModel { TransactionViewModel(androidContext(), get(), get(), get(), get()) }
//    viewModel { ReferralViewModel(get()) }
//    viewModel { ActivityViewModel(get()) }
//    viewModel { RoiViewModel(androidContext(), get(), get(), get(), get()) }
//    viewModel { DetailedBalanceViewModel(androidContext(), get(), get(), get(), get()) }
//    viewModel { TokenIssueViewModel(androidContext(), get(), get(), get()) }
//    viewModel { TokenBalanceViewModel(androidContext(), get(), get(), get(), get()) }
//    viewModel { TokenMintBurnViewModel(androidContext(), get(), get(), get()) }
//    viewModel { SmartStakingViewModel(androidContext(), get(), get(), get(), get(), get(), get()) }
//    viewModel { StakeProvidersViewModel(get(), get(), get()) }
//    viewModel { ValidatorsViewModel(get(), get()) }
//    viewModel { SmartStakingTransactionViewModel(androidContext(), get(), get(), get(), get(), get()) }
//    viewModel { ValidatorViewModel(androidContext(), get(), get(), get(), get(), get(), get()) }
}