package com.wherefam.android.core.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wherefam.android.R
import org.koin.androidx.compose.koinViewModel

val CustomOrange = Color(0xFFFFB77F)

@Composable
fun ThirdPageView(viewModel: ThirdPageViewModel = koinViewModel()) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri -> viewModel.loadImageFromUri(uri) })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ready to add your photo?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = CustomOrange,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }, contentAlignment = Alignment.Center
        ) {
            if (viewModel.userImage != null) {
                Image(
                    bitmap = viewModel.userImage!!,
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, CustomOrange, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "Placeholder Avatar",
                    colorFilter = ColorFilter.tint(CustomOrange),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .clip(CircleShape)
                        .border(2.dp, CustomOrange, CircleShape)
                )
            }

            if (viewModel.userImage == null) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add photo",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(CustomOrange, CircleShape)
                )
            }
        }
    }
}