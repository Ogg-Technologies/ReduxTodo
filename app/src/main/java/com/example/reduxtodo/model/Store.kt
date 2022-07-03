package com.example.reduxtodo.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Dispatch = (dispatchable: Dispatchable) -> Unit
typealias AsyncDispatch = suspend (dispatchable: Dispatchable) -> Unit

object Store {
    private val mutableStateFlow: MutableStateFlow<State> = MutableStateFlow(State())
    val stateFlow: StateFlow<State> get() = mutableStateFlow.asStateFlow()

    val dispatch: Dispatch = { dispatchable ->
        when (dispatchable) {
            is Action -> dispatchAction(dispatchable)
            is Thunk -> dispatchThunk(dispatchable)
            is AsyncThunk -> dispatchAsyncThunk(dispatchable)
        }

    }

    private fun dispatchAction(action: Action) {
        val state = rootReducer(mutableStateFlow.value, action)
        val json = Json.encodeToString(state)
        println("New action: ${action.name} -> $json")
        Database.writeJsonState(json)
        mutableStateFlow.value = state
    }

    private fun dispatchThunk(thunk: Thunk) {
        println("New Thunk: ${thunk.name}")
        thunk.execute(stateFlow.value, dispatch)
    }

    private fun dispatchAsyncThunk(thunk: AsyncThunk) {
        println("New AsyncThunk: ${thunk.name}")
        GlobalScope.launch(Dispatchers.Main) {
            thunk.execute(
                stateFlow.value
            ) { dispatchable ->
                withContext(Dispatchers.IO) {
                    dispatch(dispatchable)
                }
            }
        }
    }
}

@Serializable
data class State(
    val todos: List<Todo> = emptyList(),
    val todoFieldText: String = "",
    val todoIndexOpenedForDetails: Int? = null,
)

@Serializable
data class Todo(val text: String = "", val isDone: Boolean = false)

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetState -> action.state
    is TodoAction.EditFieldText -> state.copy(todoFieldText = action.text)
    is TodoAction.SubmitField -> state.copy(
        todos = todosReducer(state, action),
        todoFieldText = ""
    )
    is TodoAction -> state.copy(todos = todosReducer(state, action))
    is DetailsAction -> state.copy(todoIndexOpenedForDetails = detailsReducer(action))
    else -> state
}

fun todosReducer(state: State, action: TodoAction): List<Todo> = when (action) {
    is TodoAction.Remove -> state.todos.filterIndexed { index, _ -> index != action.index }
    is TodoAction.Toggle -> state.todos.mapIndexed { index, todo ->
        if (index == action.index) todo.copy(isDone = !todo.isDone) else todo
    }
    is TodoAction.SubmitField -> listOf(Todo(state.todoFieldText)) + state.todos
    is TodoAction.RemoveCompleted -> state.todos.filter { !it.isDone }
    else -> state.todos
}

fun detailsReducer(action: DetailsAction): Int? = when (action) {
    is DetailsAction.Open -> action.index
    is DetailsAction.Close -> null
}

fun State.selectDetailsTodo(): Todo? = todoIndexOpenedForDetails?.let { todos.getOrNull(it) }