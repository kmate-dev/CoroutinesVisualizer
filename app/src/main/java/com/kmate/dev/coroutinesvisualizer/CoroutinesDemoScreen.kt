package com.kmate.dev.coroutinesvisualizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus

@Composable
fun CoroutinesDemoScreen(
    modifier: Modifier,
    viewModel: CoroutinesDemoScreenViewModel = viewModel()
) {
    val roots by viewModel.rootCoroutines.collectAsState()

    Column(modifier) {
        Row {
            Button(onClick = viewModel::addRootCoroutine) {
                Text("Add Root Coroutine")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = viewModel::cancelAll) {
                Text("Cancel All")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(roots) { node ->
                CoroutineNodeView(
                    node = node,
                    level = 0,
                    onAddChild = { viewModel.addChildCoroutine(it) },
                    onCancel = { viewModel.cancelNode(it) }
                )
            }
        }
    }
}

@Composable
fun CoroutineNodeView(
    node: CoroutineNode,
    level: Int,
    onAddChild: (id: String) -> Unit,
    onCancel: (id: String) -> Unit
) {
    Column(
        Modifier
            .padding(start = (level * 16).dp, bottom = 8.dp)
    ) {
        Card {
            Row(
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(node.name)
                    Text(
                        text = node.status.name,
                        color = when (node.status) {
                            CoroutineStatus.Running -> Color.Green
                            CoroutineStatus.Completed -> Color.Blue
                            CoroutineStatus.Cancelled -> Color.Red
                            CoroutineStatus.Failed -> Color.Magenta
                        }
                    )
                }

                Row {
                    Button(onClick = { onAddChild(node.id) }) { Text("+ Child") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onCancel(node.id) }) { Text("Cancel") }
                }
            }
        }

        node.children.forEach { child ->
            CoroutineNodeView(
                node = child,
                level = level + 1,
                onAddChild = { onAddChild(child.id) },
                onCancel = { onCancel(child.id) }
            )
        }
    }
}

