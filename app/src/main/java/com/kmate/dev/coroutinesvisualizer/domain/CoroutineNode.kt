package com.kmate.dev.coroutinesvisualizer.domain

import java.util.UUID

data class CoroutineNode(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val status: CoroutineStatus = CoroutineStatus.Running,
    val children: List<CoroutineNode> = emptyList()
)
