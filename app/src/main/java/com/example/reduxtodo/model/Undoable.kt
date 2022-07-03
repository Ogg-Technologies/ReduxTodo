package com.example.reduxtodo.model

import kotlinx.serialization.Serializable

@Serializable
data class Undoable<T>(
    val present: T,
    val past: List<T> = emptyList(),
    val future: List<T> = emptyList(),
    val historyLength: Int = 50
)

fun <T> Undoable<T>.set(newPresent: T): Undoable<T> = copy(
    present = newPresent,
    past = (past + present).takeLast(historyLength),
    future = emptyList()
)

fun <T> Undoable<T>.edit(transform: (present: T) -> T): Undoable<T> = set(transform(present))

fun <T> Undoable<T>.undo(): Undoable<T> =
    if (past.isEmpty()) this else copy(
        present = past.last(),
        past = past.dropLast(1),
        future = (listOf(present) + future).take(historyLength)
    )

fun <T> Undoable<T>.redo(): Undoable<T> =
    if (future.isEmpty()) this else copy(
        present = future.first(),
        past = (past + present).takeLast(historyLength),
        future = future.drop(1),
    )
