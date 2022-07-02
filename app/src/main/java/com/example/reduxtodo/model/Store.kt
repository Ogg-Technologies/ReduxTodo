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
        println("${action.name} -> $json")
        Database.writeJsonState(json)
        mutableStateFlow.value = state
    }
}

@Serializable
data class State(
    val todos: List<String> = emptyList(),
    val todoFieldText: String = "",
    val todoIndexOpenedForDetails: Int? = null,
)

val Action.name: String
    get() = try {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        javaClass.canonicalName.removePrefix(javaClass.`package`.name).removePrefix(".")
    } catch (e: Exception) {
        "UnknownActionName"
    }

interface Action

class SetState(val state: State) : Action

sealed interface Todo : Action {
    class Remove(val index: Int) : Todo
    class EditFieldText(val text: String) : Todo
    object SubmitField : Todo
}

sealed interface Details : Action {
    class Open(val index: Int) : Details
    object Close : Details
}

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetState -> action.state
    is Todo.Remove -> state.copy(todos = state.todos.filterIndexed { index, _ -> index != action.index })
    is Todo.EditFieldText -> state.copy(todoFieldText = action.text)
    is Todo.SubmitField -> state.copy(
        todos = listOf(state.todoFieldText) + state.todos,
        todoFieldText = ""
    )
    is Details -> state.copy(todoIndexOpenedForDetails = detailsReducer(state, action))
    else -> state
}

fun detailsReducer(state: State, action: Details): Int? = when (action) {
    is Details.Open -> action.index
    is Details.Close -> null
}

fun State.selectDetailsTodo(): String? = todoIndexOpenedForDetails?.let { todos.getOrNull(it) }