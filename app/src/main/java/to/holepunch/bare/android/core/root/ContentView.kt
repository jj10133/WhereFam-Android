package to.holepunch.bare.android.core.root

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import to.holepunch.bare.android.core.onboarding.FifthPageView
import to.holepunch.bare.android.core.onboarding.FirstPageView
import to.holepunch.bare.android.core.onboarding.FourthPageView
import to.holepunch.bare.android.core.onboarding.OnboardingView
import to.holepunch.bare.android.core.onboarding.SecondPageView
import to.holepunch.bare.android.core.onboarding.ThirdPageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {

    OnboardingView(
        pages = listOf(
            { FirstPageView() },
            { SecondPageView() },
            { ThirdPageView() },
            { FourthPageView() },
            { FifthPageView() }
        )
    )

}
