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
    val undoableTodos: UndoableTodos = Undoable(emptyList()),
    val todoFieldText: String = "",
    val todoIndexOpenedForDetails: Int? = null,
)

@Serializable
data class Todo(val text: String = "", val isDone: Boolean = false)

fun rootReducer(state: State, action: Action): State = when (action) {
    is SetState -> action.state
    is EditFieldText -> state.copy(todoFieldText = action.text)
    is SubmitField -> state.copy(
        undoableTodos = undoableTodosReducer(
            state.undoableTodos,
            TodoAction.Add(state.todoFieldText)
        ),
        todoFieldText = ""
    )
    is TodoAction -> state.copy(undoableTodos = undoableTodosReducer(state.undoableTodos, action))
    is DetailsAction -> state.copy(todoIndexOpenedForDetails = detailsReducer(action))
    else -> state
}

typealias UndoableTodos = Undoable<List<Todo>>

fun undoableTodosReducer(undoableTodos: UndoableTodos, action: TodoAction): UndoableTodos =
    when (action) {
        is TodoAction.Remove -> undoableTodos.edit { it.remove(action.index) }
        is TodoAction.Toggle -> undoableTodos.edit { todos ->
            todos.edit(action.index) { it.copy(isDone = !it.isDone) }
        }
        is TodoAction.RemoveAllCompleted -> undoableTodos.edit { it.filter { !it.isDone } }
        is TodoAction.Add -> undoableTodos.edit { listOf(Todo(action.text)) + it }
        is TodoAction.Undo -> undoableTodos.undo()
        is TodoAction.Redo -> undoableTodos.redo()
        else -> undoableTodos
    }

fun detailsReducer(action: DetailsAction): Int? = when (action) {
    is DetailsAction.Open -> action.index
    is DetailsAction.Close -> null
}

val State.todos: List<Todo> get() = undoableTodos.present

fun State.selectDetailsTodo(): Todo? = todoIndexOpenedForDetails?.let { todos.getOrNull(it) }