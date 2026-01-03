package com.kmate.dev.coroutinesvisualizer.ui

import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode
import com.kmate.dev.coroutinesvisualizer.ui.models.PositionedNode

fun layoutTree(
    roots: List<CoroutineNode>,
    xSpacing: Float = 220f,
    ySpacing: Float = 160f
): List<PositionedNode> {

    val positioned = mutableListOf<PositionedNode>()
    var nextX = 0

    fun dfs(node: CoroutineNode, depth: Int): Float {
        if (node.children.isEmpty()) {
            val x = nextX++.toFloat()
            positioned += PositionedNode(node, x, depth.toFloat())
            return x
        }

        val childXs = node.children.map { child ->
            dfs(child, depth + 1)
        }

        val x = childXs.average().toFloat()
        positioned += PositionedNode(node, x, depth.toFloat())
        return x
    }

    roots.forEach { dfs(it, 0) }

    return positioned.map {
        it.copy(
            x = it.x * xSpacing,
            y = it.y * ySpacing
        )
    }
}
