package com.kmate.dev.coroutinesvisualizer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Coroutine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val status: String = "Running"
)

class CoroutinesDemoScreenViewModel : ViewModel() {

    private val _coroutines = MutableStateFlow<List<Coroutine>>(emptyList())
    val coroutines = _coroutines.asStateFlow()

    private val parentCoroutine = SupervisorJob()
    private val parentScope = CoroutineScope(parentCoroutine + Dispatchers.Default)

    private val coroutinesMap = mutableMapOf<String, Job>()

    fun addCoroutine() {
        val jobName = "Job ${_coroutines.value.size + 1}"
        val coroutine = Coroutine(name = jobName)

        _coroutines.value += coroutine

        val job = parentScope.launch {
            try {
                repeat(10) { i ->
                    delay(1000)
                    updateStatus(coroutine.id, "Running step ${i + 1}")
                }
                updateStatus(coroutine.id, "Completed")
            } catch (e: CancellationException) {
                updateStatus(coroutine.id, "Cancelled")
                throw e
            }
        }

        coroutinesMap[coroutine.id] = job
    }

    fun cancelCoroutine(id: String) {
        coroutinesMap[id]?.cancel()
    }

    fun cancelAll() {
        parentCoroutine.cancelChildren()
    }

    fun restartAll() {
        // optional helper to reset
        coroutinesMap.clear()
        _coroutines.value = emptyList()
    }

    private fun updateStatus(id: String, status: String) {
        _coroutines.value = _coroutines.value.map {
            if (it.id == id) it.copy(status = status) else it
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentCoroutine.cancel()
    }
}
