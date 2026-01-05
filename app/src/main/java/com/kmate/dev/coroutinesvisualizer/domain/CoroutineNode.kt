package com.kmate.dev.coroutinesvisualizer.domain

import kotlinx.coroutines.Job

data class CoroutineNode(
    val id: String,
    val status: CoroutineStatus = CoroutineStatus.Running,
    val job: Job? = null,
    val children: List<CoroutineNode> = emptyList(),
    val isSupervising: Boolean,
    val hasCoroutineExceptionHandler: Boolean,
    val caughtException: Boolean = false,
)
