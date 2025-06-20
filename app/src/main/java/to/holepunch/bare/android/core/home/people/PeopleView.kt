package to.holepunch.bare.android.core.home.people

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun PeopleView(peopleViewModel: PeopleViewModel = koinViewModel()) {
    val context = LocalContext.current
    val peopleList by peopleViewModel.peopleList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Padding around the row
            horizontalArrangement = Arrangement.End // Align icon to the right
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        showDialog = true
                    }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(peopleList) { person ->
                SwipeableItemWithActions(
                    isRevealed = person.isOptionRevealed,
                    onExpanded = {
                        peopleViewModel.updatePerson(person.copy(isOptionRevealed = true))
                    },
                    onCollapsed = {
                        peopleViewModel.updatePerson(person.copy(isOptionRevealed = false))
                    },
                    actions = {
                        ActionIcon(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Contact ${person.id} was deleted.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                peopleViewModel.removePerson(person.id)
                            },
                            backgroundColor = Color.Red,
                            icon = Icons.Default.Delete,
                            modifier = Modifier.fillMaxHeight()
                        )
                    },
                ) {
                    Text(
                        text = "Contact ${person.id}",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

    }

    if (showDialog) {
        AddPeopleDialog(
            onConfirm = { newPersonId ->
                peopleViewModel.addPerson(person = Person(newPersonId.toInt(), "Test", false))
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeopleDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var dialogInput by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            tonalElevation = AlertDialogDefaults.TonalElevation,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Add People",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(
                    value = dialogInput,
                    onValueChange = { dialogInput = it },
                    label = { Text("Enter ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = {
                            onConfirm(dialogInput)
                            dialogInput = ""
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
