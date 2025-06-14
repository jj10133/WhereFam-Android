package to.holepunch.bare.android.core.root

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject
import to.holepunch.bare.android.core.home.HomeView
import to.holepunch.bare.android.core.onboarding.*
import to.holepunch.bare.android.data_access.local.PrefUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView(prefUtils: PrefUtils = koinInject()) {

    val onboardingCompleted by prefUtils.onboardingCompletedFlow.collectAsState(initial = false)

    if (!onboardingCompleted) {
        OnboardingView(
            pages = listOf(
                { FirstPageView() },
                { SecondPageView() },
                { ThirdPageView() },
                { FourthPageView() },
                { FifthPageView() }
            )
        )
    } else {
        HomeView()
    }
}
