package to.holepunch.bare.android.core.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import to.holepunch.bare.android.R

enum class MenuOption {
    People, ShareID, ProvideFeedback
}

@Composable
fun Menu(
    onPeopleSelected: () -> Unit,
    onShareIDSelected: () -> Unit,
    onProvideFeedbackSelected: () -> Unit,
    onReferFriendSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = MaterialTheme.shapes.large,
            tonalElevation = MenuDefaults.TonalElevation,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            DropdownMenuItem(
                text = { Text("People") },
                onClick = {
                    expanded = false
                    onPeopleSelected()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Add Member"
                    )
                }
            )

            DropdownMenuItem(
                text = { Text("Share Your ID") },
                onClick = {
                    expanded = false
                    onShareIDSelected()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.round_qr_code_2_24),
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = { Text("Provide Feedback") },
                onClick = {
                    expanded = false
                    onProvideFeedbackSelected()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.material_symbols_outlined_feedback),
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Refer to friend")
                },
                onClick = {
                    expanded = false
                    onReferFriendSelected()
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                }
            )
        }
    }

}