package com.wherefam.android.core.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wherefam.android.R

enum class MenuOption {
    People, ShareID
}

@Composable
fun Menu(
    onPeopleSelected: () -> Unit,
    onShareIDSelected: () -> Unit,
    onReferFriendSelected: () -> Unit,
    onRateAppSelected: () -> Unit,
    onSupportAppSelected: () -> Unit
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

            DropdownMenuItem(
                text = { Text("Rate App") },
                onClick = {
                    expanded = false
                    onRateAppSelected()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_rate_review_24),
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = { Text("Support App") },
                onClick = {
                    expanded = false
                    onSupportAppSelected()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_wand_stars_24),
                        contentDescription = null
                    )
                }
            )
        }
    }

}