package to.holepunch.bare.android.core.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FirstPageView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Welcome!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFB77F),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            imageVector = Icons.Default.FamilyRestroom,
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color(0xFFFFB77F)),
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Stay connected with your loved ones ",
                fontSize = 20.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Globally, Securely and Privately.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB77F),
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}