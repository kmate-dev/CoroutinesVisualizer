package com.kmate.dev.coroutinesvisualizer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.warningFlow.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(Modifier.fillMaxSize()) {

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
                    onThrowException = { viewModel.throwException(it) },
                    onCancel = { viewModel.cancelNode(it) }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

