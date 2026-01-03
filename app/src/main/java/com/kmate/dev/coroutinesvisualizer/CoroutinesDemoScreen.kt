package com.kmate.dev.coroutinesvisualizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CoroutinesDemoScreen(
    modifier: Modifier,
    viewModel: CoroutinesDemoScreenViewModel = viewModel()
) {
    val coroutines by viewModel.coroutines.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Coroutines Demo", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))

        Row {
            Button(onClick = { viewModel.addCoroutine() }) {
                Text("Add Coroutine")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = { viewModel.cancelAll() }) {
                Text("Cancel All")
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(onClick = { viewModel.restartAll() }) {
                Text("Clear")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(coroutines) { coroutine ->
                Card {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(coroutine.name, style = MaterialTheme.typography.titleMedium)
                            Text(coroutine.status, style = MaterialTheme.typography.bodyMedium)
                        }

                        Button(onClick = { viewModel.cancelCoroutine(coroutine.id) }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}
