package com.example.reduxtodo.model

interface Dispatchable

val Dispatchable.name: String
    get() = try {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        javaClass.canonicalName.removePrefix(javaClass.`package`.name).removePrefix(".")
    } catch (e: Exception) {
        "UnknownActionName"
    }

interface Action : Dispatchable

class SetState(val state: State) : Action

sealed interface TodoAction : Action {
    class Remove(val index: Int) : TodoAction
    class EditFieldText(val text: String) : TodoAction
    object SubmitField : TodoAction
    class Toggle(val index: Int) : TodoAction
}

sealed interface DetailsAction : Action {
    class Open(val index: Int) : DetailsAction
    object Close : DetailsAction
}

class Thunk(val execute: (state: State, dispatch: Dispatch) -> Unit) : Dispatchable

fun doToggleDetailedTodo(): Thunk = Thunk { state, dispatch ->
    val detailsIndex = state.todoIndexOpenedForDetails ?: return@Thunk
    dispatch(TodoAction.Toggle(detailsIndex))
}