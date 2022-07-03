package com.example.reduxtodo.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

@Composable
fun MainScreen(state: State, dispatch: Dispatch = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ReduxTodo") },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                TodoAddingForm(
                    state.todoFieldText,
                    { text -> dispatch(TodoAction.EditFieldText(text)) },
                    { dispatch(TodoAction.SubmitField) }
                )
                TodoList(state.todos, dispatch)
            }
        }
    )
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
        placeholder = { Text("Add a todo...") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TodoList(todos: List<Todo>, dispatch: Dispatch) {
    LazyColumn {
        todos.forEachIndexed { index, todo ->
            item {
                TodoItem(
                    todo,
                    onDelete = {
                        dispatch(TodoAction.Remove(index))
                    },
                    onOpened = {
                        dispatch(DetailsAction.Open(index))
                    },
                    onCheckedChanged = {
                        dispatch(TodoAction.Toggle(index))
                    }
                )
            }
        }
    }
}

@Composable
fun TodoItem(todo: Todo, onDelete: () -> Unit, onOpened: () -> Unit, onCheckedChanged: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onOpened() },
    ) {
        Checkbox(checked = todo.isDone, onCheckedChange = onCheckedChanged)
        Text(text = todo.text, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

fun createMockState() = State(todos = listOf("Chill", "Drink", "Eat").map { Todo(it) })

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainPreview() {
    ReduxTodoTheme {
        MainScreen(state = createMockState())
    }
}
