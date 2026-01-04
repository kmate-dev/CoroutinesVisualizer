package com.kmate.dev.coroutinesvisualizer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineStatus
import com.kmate.dev.coroutinesvisualizer.ui.toStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoroutinesDemoScreenViewModel : ViewModel() {
    private val _rootCoroutines = MutableStateFlow<List<CoroutineNode>>(emptyList())
    val rootCoroutines = _rootCoroutines.asStateFlow()

    private val mainJob = SupervisorJob()
    private val mainScope = CoroutineScope(mainJob + Dispatchers.Default)

    private val jobMap = mutableMapOf<String, Job>()

    fun addRootCoroutine() {
        addCoroutineInternal(null)
    }

    fun addChildCoroutine(parentNode: CoroutineNode) {
        addCoroutineInternal(parentNode)
    }

    private fun addCoroutineInternal(parentNode: CoroutineNode?) {
        val newId =
            if (parentNode == null) "Coroutine ${_rootCoroutines.value.size + 1}"
            else "${parentNode.id}.${parentNode.children.size + 1}"

        val newNode = CoroutineNode(
            id = newId,
        )


        if (parentNode == null)
            _rootCoroutines.value += newNode
        else {
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
        launchTree()
    }

    private fun launchTree() {
        rootCoroutines.value.forEach { rootNode ->
            val job = launchNode(rootNode, mainScope)
            _rootCoroutines.update {
                _rootCoroutines.value.updateNode(rootNode.id) {
                    it.copy(
                        job = job,
                        status = job.toStatus()
                    )
                }
            }
        }
    }

    private fun launchNode(node: CoroutineNode, parentScope: CoroutineScope): Job {
        val job = parentScope.launch {
            if (node.children.isEmpty()) {
                while(true) {
                    delay(10)
                }
            } else {
                node.children.forEach { childNode ->
                    val job = launchNode(childNode, this)
                    _rootCoroutines.update {
                        _rootCoroutines.value.updateNode(childNode.id) {
                            it.copy(
                                job = job,
                                status = job.toStatus()
                            )
                        }
                    }
                }
            }
        }

        job.invokeOnCompletion { error ->
            val status = when (error) {
                null -> CoroutineStatus.Completed
                is CancellationException -> CoroutineStatus.Cancelled
                else -> CoroutineStatus.Failed
            }

            updateStatus(node.id, status)
        }
        return job
    }

    fun cancelNode(node: CoroutineNode) {
        node.job?.cancel()
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
}

private fun List<CoroutineNode>.updateNode(
    id: String,
    transform: (CoroutineNode) -> CoroutineNode
): List<CoroutineNode> =
    map {
        if (it.id == id) transform(it)
        else it.copy(children = it.children.updateNode(id, transform))
    }


