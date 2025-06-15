package to.holepunch.bare.android.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import to.holepunch.bare.android.core.home.HomeViewModel
import to.holepunch.bare.android.core.onboarding.OnboardingViewModel
import to.holepunch.bare.android.core.onboarding.SplashViewModel
import to.holepunch.bare.android.core.onboarding.ThirdPageViewModel
import to.holepunch.bare.android.data.ipc.IPCProvider
import to.holepunch.bare.android.data.local.DataStoreRepository
import to.holepunch.bare.android.manager.LocationManager

val appModule = module {
    single { LocationManager(get()) }

    single { IPCProvider.ipc }

    single { DataStoreRepository(get()) }
}

val viewModel = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { ThirdPageViewModel(get(), get()) }

    viewModel { HomeViewModel(get(), get()) }

}
