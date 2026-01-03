package com.kmate.dev.coroutinesvisualizer

import androidx.lifecycle.ViewModel
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class CoroutinesDemoScreenViewModel : ViewModel() {

    private val _rootCoroutines = MutableStateFlow<List<CoroutineNode>>(emptyList())
    val rootCoroutines = _rootCoroutines.asStateFlow()

    private val parentJob = SupervisorJob()
    private val parentScope = CoroutineScope(parentJob + Dispatchers.Default)

    private val jobMap = mutableMapOf<String, Job>()

    fun addRootCoroutine() {
        addCoroutineInternal(parentScope, null)
    }

    fun addChildCoroutine(parentId: String) {
        val parentJob = jobMap[parentId] ?: return
        val childScope = CoroutineScope(parentJob)
        addCoroutineInternal(childScope, parentId)
    }

    private fun addCoroutineInternal(scope: CoroutineScope, parentId: String?) {
        val name =
            if (parentId == null) "Coroutine ${_rootCoroutines.value.size + 1}"
            else "Child ${UUID.randomUUID().toString().take(4)}"

        val node = CoroutineNode(name = name)

        _rootCoroutines.value =
            if (parentId == null)
                _rootCoroutines.value + node
            else
                _rootCoroutines.value.updateNode(parentId) { it.copy(children = it.children + node) }

        val job = scope.launch {

            repeat(10) { i ->
                delay(1000)
            }
        }

        job.invokeOnCompletion { error ->
            val status = when {
                error == null -> CoroutineStatus.Completed
                error is CancellationException -> CoroutineStatus.Cancelled
                else -> CoroutineStatus.Failed
            }

            updateStatus(node.id, status)
        }

        jobMap[node.id] = job
    }

    fun cancelNode(id: String) {
        jobMap[id]?.cancel()
    }

    fun clearAll() {
        parentJob.cancelChildren()
        _rootCoroutines.update { emptyList() }
        jobMap.clear()
    }

    private fun updateStatus(id: String, status: CoroutineStatus) {
        _rootCoroutines.value = _rootCoroutines.value.updateNode(id) {
            it.copy(status = status)
        }
    }

    override fun onCleared() {
        parentJob.cancel()
    }
}

private fun List<CoroutineNode>.updateNode(
    id: String,
    transform: (CoroutineNode) -> CoroutineNode
): List<CoroutineNode> =
    map {
        if (it.id == id) transform(it)
        else it.copy(children = it.children.updateNode(id, transform))
    }


