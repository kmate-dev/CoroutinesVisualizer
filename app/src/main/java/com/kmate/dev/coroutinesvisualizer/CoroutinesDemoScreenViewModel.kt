package com.kmate.dev.coroutinesvisualizer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNodeEvent
import com.kmate.dev.coroutinesvisualizer.ui.toStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class CoroutinesDemoScreenViewModel : ViewModel() {
    companion object {
        private const val TAG = "DEMO"
    }
    private val _rootCoroutines = MutableStateFlow<List<CoroutineNode>>(emptyList())
    val rootCoroutines = _rootCoroutines.asStateFlow()

    private val _warningFlow = MutableSharedFlow<String>()
    val warningFlow = _warningFlow.asSharedFlow()

    private val mainExceptionHandler = CoroutineExceptionHandler { _, exception ->

        Log.w(TAG, "Main Scope caught exception: $exception")
        viewModelScope.launch {
            _warningFlow.emit("MainScope caught exception - app would crash now without " +
                    "custom CoroutineExceptionHandler"
            )
        }
    }
    private val mainJob = SupervisorJob()
    private val mainScope = CoroutineScope(mainJob + Dispatchers.Default + mainExceptionHandler)

    private val jobMap = mutableMapOf<String, Job>()

    private val nodeEvents = MutableSharedFlow<CoroutineNodeEvent>()

    fun addCoroutine(parentNode: CoroutineNode?, isSupervised: Boolean) {
        viewModelScope.launch {
            if (parentNode == null) {
                _rootCoroutines.value += createNewNode(null, mainScope, isSupervised)
            } else {
                nodeEvents.emit(
                    CoroutineNodeEvent.AddChildNode(
                        nodeId = parentNode.id,
                        isSupervised
                    )
                )
            }
        }
    }

    fun cancelNode(node: CoroutineNode) {
        node.job?.cancel()
    }

    fun throwException(node: CoroutineNode) {
        viewModelScope.launch {
            nodeEvents.emit(CoroutineNodeEvent.ThrowException(node.id))
        }
    }

    fun clearAll() {
        mainJob.cancelChildren()
        _rootCoroutines.update { emptyList() }
        jobMap.clear()
    }

    private fun updateStatus(id: String, status: CoroutineStatus) {
        _rootCoroutines.update {
            _rootCoroutines.value.updateNode(id) {
                it.copy(status = status)
            }
        }
    }

    override fun onCleared() {
        mainJob.cancel()
    }

    private suspend fun observeEvents(observerNodeId: String, scope: CoroutineScope) {
        nodeEvents.collect { event ->
            if(event.nodeId == observerNodeId) {
                when (event) {
                    is CoroutineNodeEvent.ThrowException -> {
                        throw Exception("User thrown exception")
                    }

                    is CoroutineNodeEvent.AddChildNode -> {
                        val parentNode = rootCoroutines.value.findNodeById(observerNodeId) ?: return@collect

                        val newNode = createNewNode(parentNode, scope, event.isSupervised)

                        _rootCoroutines.update {
                            _rootCoroutines.value.updateNode(parentNode.id) {
                                val newChildrenList = it.children.toMutableList().apply {
                                    add(newNode)
                                }
                                it.copy(
                                    children = newChildrenList
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    private fun createNewNode(parentNode: CoroutineNode?, parentScope: CoroutineScope, isSupervised: Boolean): CoroutineNode {
        val newId =
            if (parentNode == null) "Coroutine ${_rootCoroutines.value.size + 1}"
            else "${parentNode.id}.${parentNode.children.size + 1}"

        val newJob =
            parentScope.launch {
                if (isSupervised) {
                    supervisorScope {
                        observeEvents(newId, this)
                    }
                } else {
                    observeEvents(newId, this)
                }
            }

        newJob.invokeOnCompletion { error ->
            val status = when (error) {
                null -> CoroutineStatus.Completed
                is CancellationException -> CoroutineStatus.Cancelled
                else -> CoroutineStatus.Failed
            }
            updateStatus(newId, status)
        }

        return CoroutineNode(
            id = newId,
            job = newJob,
            status = newJob.toStatus(),
            isSupervised = isSupervised,
        )
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

private fun List<CoroutineNode>.findNodeById(
    id: String,
): CoroutineNode? {
    return this.find { it.id == id } ?:
    this.firstNotNullOfOrNull {
        it.children.findNodeById(id)
    }
}


