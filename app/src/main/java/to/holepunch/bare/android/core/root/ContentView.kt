package to.holepunch.bare.android.core.root

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import to.holepunch.bare.android.core.home.HomeView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {
  Box {
      HomeView()
  }
}


