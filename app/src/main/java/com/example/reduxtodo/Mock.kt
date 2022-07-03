package com.example.reduxtodo

import com.example.reduxtodo.model.State
import com.example.reduxtodo.model.Todo
import com.example.reduxtodo.model.Undoable

fun createMockState(detailsOpened : Boolean = false) = State(
    undoableTodos = Undoable(createMockTodos()),
    todoIndexOpenedForDetails = if (detailsOpened) 0 else null,
)

fun createMockTodos() = listOf("Chill", "Drink", "Eat").map { Todo(it) }
