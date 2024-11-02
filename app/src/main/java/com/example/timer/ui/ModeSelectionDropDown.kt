package com.example.timer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timer.data.Mode
import com.example.timer.ui.vm.TimerViewModel

@Composable
fun ModeSelectionDropDown(
    mode: Mode,
    timerViewModel: TimerViewModel
) {
    var isModeMenuExpanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth() ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Select Timer Mode")
            Spacer(Modifier.weight(1f))
            Text(":", fontSize = 25.sp)
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Row(modifier = Modifier
                    .clickable { isModeMenuExpanded = !isModeMenuExpanded }
                    .background(color = Color.White.copy(alpha = 0.1f))
                    .padding(10.dp)
                ) {
                    Text(mode.name)
                    Spacer(Modifier.size(20.dp))
                    Icon(
                        imageVector = if (isModeMenuExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        Modifier.clickable { isModeMenuExpanded = !isModeMenuExpanded }
                    )
                }
                DropdownMenu(
                    expanded = isModeMenuExpanded,
                    onDismissRequest = { isModeMenuExpanded = false },
                    modifier = Modifier
                        .padding(10.dp)
//                .fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        onClick = {
                            timerViewModel.changeMode(Mode.OddEven)
                            isModeMenuExpanded = false
                        },
                        text = { Text("Odd-Even Timer") }
                    )
                    DropdownMenuItem(
                        onClick = {
                            timerViewModel.changeMode(Mode.Random)
                            isModeMenuExpanded = false
                        },
                        text = { Text("Random Timer") }
                    )
                }
            }
        }

    }

}

@Preview
@Composable
fun MenuSample() {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = { /* Handle edit! */ },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
            )
            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = { /* Handle settings! */ },
                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Send Feedback") },
                onClick = {},
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
            )
        }
    }
}
