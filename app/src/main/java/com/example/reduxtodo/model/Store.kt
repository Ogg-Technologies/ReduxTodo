package com.example.reduxtodo.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object Store {
    private val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(State())
    val stateFlow: StateFlow<State> get() = mutableStateFlow.asStateFlow()

    fun dispatch(action: Action) {
        mutableStateFlow.value = rootReducer(mutableStateFlow.value, action)
    }
}

fun createMockState() = State(todos = listOf("Chill", "Drink", "Eat"))

data class State(
    val todos: List<String> = emptyList(),
    val todoFieldText: String = ""
)

interface Action

class RemoveTodo(val index: Int) : Action
class EditTodoFieldText(val text: String) : Action
class SubmitTodoField : Action

fun rootReducer(state: State, action: Action): State = when (action) {
    is RemoveTodo -> state.copy(todos = state.todos.filterIndexed { index, _ -> index != action.index })
    is EditTodoFieldText -> state.copy(todoFieldText = action.text)
    is SubmitTodoField -> state.copy(todos = state.todos + state.todoFieldText, todoFieldText = "")
    else -> state
}