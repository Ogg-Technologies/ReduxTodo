package com.example.reduxtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tryLoadSavedState()
        setContent {
            ReduxTodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val state: State by Store.stateFlow.collectAsState()
                    val details = state.selectDetailsTodo()
                    if (details != null) {
                        DetailsScreen(todo = details, Store.dispatch)
                    } else {
                        MainScreen(state, Store.dispatch)
                    }
                }
            }
        }
    }

    private fun tryLoadSavedState() {
        val savedJsonState = Database.readJsonState() ?: return
        try {
            val state = Json.decodeFromString(State.serializer(), savedJsonState)
            Store.dispatch(SetState(state))
        } catch (e: Exception) {
        }
    }
}

@Composable
fun DetailsScreen(todo: String, dispatch: Dispatch = {}) {
    BackHandler {
        dispatch(Details.Close)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = todo,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun MainScreen(state: State, dispatch: Dispatch = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TodoAddingForm(
            state.todoFieldText,
            { text -> dispatch(Todo.EditFieldText(text)) },
            { dispatch(Todo.SubmitField) }
        )
        TodoList(state.todos, dispatch)
    }
}

@Composable
fun TodoAddingForm(currentText: String, onValueChanged: (String) -> Unit, onSubmit: () -> Unit) {
    TextField(
        value = currentText,
        onValueChange = onValueChanged,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onSubmit() }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TodoList(todos: List<String>, dispatch: Dispatch) {
    LazyColumn {
        todos.forEachIndexed { index, todo ->
            item {
                TodoItem(
                    todo,
                    onDelete = {
                        dispatch(Todo.Remove(index))
                    },
                    onClick = {
                        dispatch(Details.Open(index))
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItem(todo: String, onDelete: () -> Unit, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
    ) {
        Text(text = todo, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

fun createMockState() = State(todos = listOf("Chill", "Drink", "Eat"))

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(state = createMockState())
}

@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    DetailsScreen("Chill", {})
}