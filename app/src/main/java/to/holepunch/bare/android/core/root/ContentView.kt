package to.holepunch.bare.android.core.root

import OnboardingView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import to.holepunch.bare.android.core.onboarding.FirstPageView
import to.holepunch.bare.android.core.onboarding.SecondPageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {

    OnboardingView(
        pages = listOf(
            { FirstPageView() },
            { SecondPageView() }
        )
    )

//    Box { HomeView() }
}
