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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.createMockState
import com.example.reduxtodo.model.*
import com.example.reduxtodo.model.State
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

@Composable
fun MainScreen(state: State, dispatch: Dispatch = {}) {
    Scaffold(
        topBar = { TodoAppBar(onRemoveCompleted = { dispatch(TodoAction.RemoveAllCompleted) }) },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                TodoAddingForm(
                    state.todoFieldText,
                    { text -> dispatch(EditFieldText(text)) },
                    { dispatch(SubmitField) }
                )
                TodoList(state.todos, dispatch)
            }
        },
        bottomBar = { TodoBottomBar(dispatch) }
    )
}

@Composable
private fun TodoBottomBar(dispatch: Dispatch) {
    Surface(color = MaterialTheme.colors.surface, elevation = 1.dp) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HistoryButton("Undo") { dispatch(TodoAction.Undo) }
            HistoryButton("Redo") { dispatch(TodoAction.Redo) }
        }
    }
}

@Composable
private fun HistoryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.padding(8.dp),
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}

@Composable
private fun TodoAppBar(onRemoveCompleted: () -> Unit) {
    TopAppBar(
        title = { Text("ReduxTodo") },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            var showMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(onClick = {
                    onRemoveCompleted()
                    showMenu = false
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Remove completed")
                }
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
                        dispatch(doScreenChangeDispatch(DetailsAction.Open(index)))
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
fun TodoItem(
    todo: Todo,
    onDelete: () -> Unit,
    onOpened: () -> Unit,
    onCheckedChanged: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpened() }
            .padding(16.dp),
    ) {
        Checkbox(checked = todo.isDone, onCheckedChange = onCheckedChanged)
        Text(text = todo.text, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainPreview() {
    ReduxTodoTheme {
        MainScreen(createMockState())
    }
}
