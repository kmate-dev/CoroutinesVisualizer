package com.kmate.dev.coroutinesvisualizer.ui.models

import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode

data class PositionedNode(
    val node: CoroutineNode,
    val x: Float,
    val y: Float
)