package com.kmate.dev.coroutinesvisualizer.domain

sealed interface CoroutineNodeEvent {
    val nodeId: String
    data class AddChildNode(
        override val nodeId: String,
        val isSupervised: Boolean,
    ): CoroutineNodeEvent
    data class ThrowException(override val nodeId: String): CoroutineNodeEvent
}