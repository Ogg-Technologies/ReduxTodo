package com.example.reduxtodo.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Dispatch = (Action) -> Unit

object Store {
    private val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(State())
    val stateFlow: StateFlow<State> get() = mutableStateFlow.asStateFlow()

    val dispatch: Dispatch = { action ->
        val state = rootReducer(mutableStateFlow.value, action)
        Json.encodeToString(state).also {
            println("state: $it")
        }
        mutableStateFlow.value = state
    }
}

fun createMockState() = State(todos = listOf("Chill", "Drink", "Eat"))

@Serializable
data class State(
    val todos: List<String> = emptyList(),
    val todoFieldText: String = ""
)

@Serializable
data class Test(val a: String)

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