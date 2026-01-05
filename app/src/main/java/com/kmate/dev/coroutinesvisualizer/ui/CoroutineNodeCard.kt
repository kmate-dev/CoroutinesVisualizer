package com.kmate.dev.coroutinesvisualizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus

@Composable
fun CoroutineNodeCard(
    node: CoroutineNode,
    onAddChild: () -> Unit,
    onThrowException: () -> Unit,
    onCancel: () -> Unit
) {
    val buttonsEnabled = node.status == CoroutineStatus.Running
    Card(modifier = Modifier
        .width(300.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (node.hasCoroutineExceptionHandler) {
                HorizontalLabel(
                    label = "CoroutineExceptionHandler { ... }" +
                            if (node.caughtException) "\nGot an uncaught exception"
                            else "",
                    color = Color.Yellow,
                )
            }
            Text(node.id, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = node.status.name,
                style = MaterialTheme.typography.headlineSmall,
                color = when (node.status) {
                    CoroutineStatus.Running -> Color.Green
                    CoroutineStatus.Completed -> Color.Blue
                    CoroutineStatus.Cancelled -> Color.Magenta
                    CoroutineStatus.Failed -> Color.Red
                }
            )

            Row {
                Button(onClick = onCancel, enabled = buttonsEnabled) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onThrowException, enabled = buttonsEnabled) { Text("Throw Exception") }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAddChild, enabled = buttonsEnabled) { Text("Add child coroutine") }

            if(node.isSupervising) {
                HorizontalLabel(
                    label = "supervisorScope { ... }",
                    color = Color.Green
                )
            }
        }
    }
}

@Composable
fun HorizontalLabel(
    label: String,
    color: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                color = color,
                shape = CardDefaults.shape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = label
        )
    }
}
