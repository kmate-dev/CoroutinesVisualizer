package com.kmate.dev.coroutinesvisualizer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kmate.dev.coroutinesvisualizer.ui.CoroutineTree
import com.kmate.dev.coroutinesvisualizer.ui.ZoomableTree

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

            Button(onClick = viewModel::clearAll) {
                Text("Clear All")
            }
        }

        Spacer(Modifier.height(16.dp))

        ZoomableTree {
            CoroutineTree(
                roots = roots,
                onAddChild = { viewModel.addChildCoroutine(it) },
                onCancel = { viewModel.cancelNode(it) }
            )
        }
    }
}

