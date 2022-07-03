package com.example.reduxtodo.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.createMockTodos
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

@Composable
fun DetailsScreen(state: State, dispatch: Dispatch = {}) {
    fun close() = dispatch(doScreenChangeDispatch(DetailsAction.Close))
    val todo = state.selectDetailsTodo()
    requireNotNull(todo) { "DetailsScreen cannot be opened when selectDetailsTodo is null" }
    BackHandler { close() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(
                        onClick = { close() },
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = todo.text,
                    fontSize = 60.sp,
                    textAlign = TextAlign.Center,
                )
                Checkbox(
                    checked = todo.isDone,
                    onCheckedChange = { dispatch(doToggleDetailedTodo()) },
                    modifier = Modifier.scale(3f),
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                ) {
                    Arrow(state, dispatch, ArrowDirection.BACK)
                    Arrow(state, dispatch, ArrowDirection.FORWARD)
                }
            }
        }
    )
}

enum class ArrowDirection { BACK, FORWARD }

@Composable
private fun Arrow(state: State, dispatch: Dispatch, direction: ArrowDirection) {
    val nextTodoIndex: Int? = state.todoIndexOpenedForDetails
        ?.let {
            it + when (direction) {
                ArrowDirection.BACK -> -1
                ArrowDirection.FORWARD -> 1
            }
        }
        ?.let { if (it in state.todos.indices) it else null }
    IconButton(
        onClick = {
            if (nextTodoIndex != null) {
                dispatch(DetailsAction.Open(nextTodoIndex))
            }
        },
        enabled = nextTodoIndex != null,
        modifier = Modifier.scale(3f),
    ) {
        when (direction) {
            ArrowDirection.BACK ->
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            ArrowDirection.FORWARD ->
                Icon(Icons.Filled.ArrowForward, contentDescription = "Forward")
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailsPreview() {
    ReduxTodoTheme {
        DetailsScreen(
            State(
                todos = createMockTodos(),
                todoIndexOpenedForDetails = 0
            )
        )
    }
}
