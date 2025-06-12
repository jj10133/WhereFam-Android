package to.holepunch.bare.android.core.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SecondPageView() {
    var userName by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFB77F))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "What's your name?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = userName,
                onValueChange = { newValue -> userName = newValue },
                placeholder = { Text("Enter your name", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(54.dp),
                singleLine = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Background color (container) for all states
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    errorContainerColor = Color.White,

                    // Border color for all states, matching SwiftUI's .stroke(.white)
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White,
                    errorBorderColor = Color.Red, // Keeping error red for usability

                    // Text color for input text
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Gray, // Example for disabled state
                    errorTextColor = Color.Black,

                    // Cursor color
                    cursorColor = Color.Black,
                    errorCursorColor = Color.Red, // Keeping error red
                )
            )
        }
    }
}