package com.kmate.dev.coroutinesvisualizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus

@Composable
fun CoroutineNodeCard(
    node: CoroutineNode,
    onAddChild: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.widthIn(min = 180.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(node.id)
            Text(
                text = node.status.name,
                color = when (node.status) {
                    CoroutineStatus.Running -> Color.Green
                    CoroutineStatus.Completed -> Color.Blue
                    CoroutineStatus.Cancelled -> Color.Red
                    CoroutineStatus.Failed -> Color.Magenta
                }
            )

            Row {
                Button(onClick = onAddChild) { Text("+") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onCancel) { Text("X") }
            }
        }
    }
}
