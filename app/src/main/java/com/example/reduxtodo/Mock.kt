package com.example.reduxtodo

import com.example.reduxtodo.model.Todo

fun createMockTodos() = listOf("Chill", "Drink", "Eat").map { Todo(it) }
