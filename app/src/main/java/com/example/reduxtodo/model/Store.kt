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
        val json = Json.encodeToString(state)
        Database.writeJsonState(json)
        mutableStateFlow.value = state
    }
}

@Serializable
data class State(
    val todos: List<String> = emptyList(),
    val todoFieldText: String = ""
)

interface Action
class SetState(val state: State) : Action
class RemoveTodo(val index: Int) : Action
class EditTodoFieldText(val text: String) : Action
class SubmitTodoField : Action

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetState -> action.state
    is RemoveTodo -> state.copy(todos = state.todos.filterIndexed { index, _ -> index != action.index })
    is EditTodoFieldText -> state.copy(todoFieldText = action.text)
    is SubmitTodoField -> state.copy(todos = state.todos + state.todoFieldText, todoFieldText = "")
    else -> state
}