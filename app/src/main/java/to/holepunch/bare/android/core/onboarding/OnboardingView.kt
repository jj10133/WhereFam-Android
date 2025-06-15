package to.holepunch.bare.android.core.onboarding

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingView(
    navController: NavHostController,
    pages: List<@Composable () -> Unit>,
    onboardingViewModel: OnboardingViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val targetBackgroundColor = if (pagerState.currentPage % 2 == 0) {
        Color.White
    } else {
        CustomOrange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(targetBackgroundColor),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            pages[page]()
        }

        val isLastPage = pagerState.currentPage == pages.lastIndex

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.9f else 1f,
            animationSpec = tween(durationMillis = 150)
        )

        val targetButtonContainerColor = if (pagerState.currentPage % 2 == 0) {
            CustomOrange
        } else {
            Color.White
        }
        val targetButtonContentColor = if (pagerState.currentPage % 2 == 0) {
            Color.White
        } else {
            CustomOrange
        }

        val animatedButtonContainerColor by animateColorAsState(
            targetValue = targetButtonContainerColor,
            animationSpec = tween(durationMillis = 500)
        )
        val animatedButtonContentColor by animateColorAsState(
            targetValue = targetButtonContentColor,
            animationSpec = tween(durationMillis = 500)
        )

        Button(
            onClick = {
                // Trigger vibration
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // For API 31 (Android 12) and above, use VibratorManager
                    (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
                } else {
                    // For older APIs, use the direct Vibrator service
                    @Suppress("DEPRECATION") // Suppress deprecation warning for older Vibrator methods
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))

                if (!isLastPage) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    coroutineScope.launch {
                        onboardingViewModel.saveOnboardingState(completed = true)
                        navController.popBackStack()
                        navController.navigate("Home")
                    }
                }
            },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .size(72.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedButtonContainerColor,
                contentColor = animatedButtonContentColor
            ),
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = if (isLastPage) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isLastPage) "Finish Onboarding" else "Next Page",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}