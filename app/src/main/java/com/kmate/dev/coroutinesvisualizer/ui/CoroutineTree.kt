package com.kmate.dev.coroutinesvisualizer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kmate.dev.coroutinesvisualizer.domain.CoroutineNode

@Composable
fun CoroutineTree(
    roots: List<CoroutineNode>,
    onAddChild: (CoroutineNode) -> Unit,
    onThrowException: (CoroutineNode) -> Unit,
    onCancel: (CoroutineNode) -> Unit,
) {
    val positioned = remember(roots) { layoutTree(roots) }

    Box {
        val density = LocalDensity.current
        // Draw connections
        Canvas(Modifier.fillMaxSize()) {
            positioned.forEach { node ->
                node.node.children.forEach { child ->
                    val childPos = positioned.first { it.node.id == child.id }
                    val pxXValue = density.run { (node.x.dp + 90f.dp).toPx() }
                    val pxYValue = density.run { (node.y.dp + 60f.dp).toPx() }
                    val pxXValueChild = density.run { (childPos.x.dp + 90f.dp).toPx() }
                    val pxYValueChild = density.run { (childPos.y.dp).toPx() }

                    drawLine(
                        start = Offset(pxXValue, pxYValue),
                        end = Offset(pxXValueChild, pxYValueChild),
                        color = Color.Blue
                    )
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
            positioned.forEach { pos ->
                Box(
                    Modifier
                        .absoluteOffset(
                            x = pos.x.dp,
                            y = pos.y.dp
                        )
                ) {
                    CoroutineNodeCard(
                        node = pos.node,
                        onAddChild = { onAddChild(pos.node) },
                        onThrowException = { onThrowException(pos.node) },
                        onCancel = { onCancel(pos.node) },
                    )
                }
            }
        }

    }
}
