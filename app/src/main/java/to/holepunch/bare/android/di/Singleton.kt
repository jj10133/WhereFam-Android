package to.holepunch.bare.android.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import to.holepunch.bare.android.core.home.HomeViewModel
import to.holepunch.bare.android.core.onboarding.ThirdPageViewModel
import to.holepunch.bare.android.data_access.ipc.IPCProvider
import to.holepunch.bare.android.data_access.local.PrefUtils
import to.holepunch.bare.android.manager.LocationManager

val appModule = module {
    single { LocationManager(get()) }

    single { IPCProvider.ipc }

    single { PrefUtils(get()) }
}

val viewModel = module {
    viewModel { HomeViewModel(get(), get()) }

    viewModel { ThirdPageViewModel(get(), get()) }
}
