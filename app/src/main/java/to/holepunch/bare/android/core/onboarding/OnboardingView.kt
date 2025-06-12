import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OnboardingView(pages: List<@Composable () -> Unit>) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = pagerState) { page ->
            // Content for each page
            pages[page]()
        }
    }
}
