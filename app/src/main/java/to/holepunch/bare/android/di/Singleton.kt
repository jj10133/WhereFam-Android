package to.holepunch.bare.android.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import to.holepunch.bare.android.core.home.HomeViewModel
import to.holepunch.bare.android.core.home.people.PeopleViewModel
import to.holepunch.bare.android.core.home.share.ShareViewModel
import to.holepunch.bare.android.core.onboarding.OnboardingViewModel
import to.holepunch.bare.android.core.onboarding.SplashViewModel
import to.holepunch.bare.android.core.onboarding.ThirdPageViewModel
import to.holepunch.bare.android.data.UserRepository
import to.holepunch.bare.android.data.ipc.IPCProvider
import to.holepunch.bare.android.data.ipc.UserRepositoryImpl
import to.holepunch.bare.android.data.local.DataStoreRepository
import to.holepunch.bare.android.manager.LocationManager
import to.holepunch.bare.android.processing.GenericMessageProcessor

val appModule = module {
    single { LocationManager(get()) }

    single { IPCProvider.ipc }

    single { DataStoreRepository(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    single { GenericMessageProcessor(get()) }
}

val viewModel = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { ThirdPageViewModel(get(), get()) }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { ShareViewModel(get()) }
    viewModel { PeopleViewModel() }

}
