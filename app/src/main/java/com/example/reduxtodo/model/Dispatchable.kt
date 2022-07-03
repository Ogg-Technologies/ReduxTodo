package com.example.reduxtodo.model

import kotlinx.coroutines.delay

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

class AsyncThunk(val execute: suspend (state: State, dispatch: AsyncDispatch) -> Unit) :
    Dispatchable

fun doToggleDetailedTodo(): Thunk = Thunk { state, dispatch ->
    val detailsIndex = state.todoIndexOpenedForDetails ?: return@Thunk
    dispatch(TodoAction.Toggle(detailsIndex))
}

fun doDelayedDispatch(
    dispatchable: Dispatchable,
    delay: Long = 1000
): AsyncThunk = AsyncThunk { state, dispatch ->
    delay(delay)
    dispatch(dispatchable)
}

const val SCREEN_CHANGE_DELAY: Long = 20
fun doScreenChangeDispatch(dispatchable: Dispatchable): AsyncThunk =
    doDelayedDispatch(dispatchable, SCREEN_CHANGE_DELAY)