package to.holepunch.bare.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import to.holepunch.bare.android.di.appModule
import to.holepunch.bare.android.di.viewModel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule, viewModel)
        }
    }
}