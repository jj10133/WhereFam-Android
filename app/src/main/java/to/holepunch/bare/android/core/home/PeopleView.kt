package to.holepunch.bare.android.core.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PeopleView() {
    LazyColumn {
        items(10) { index ->
            BasicText("Item #$index")
        }
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
                modifier = Modifier.padding(16.dp)
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

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ){
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
