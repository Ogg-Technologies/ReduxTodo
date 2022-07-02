package com.example.reduxtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state: State by Store.stateFlow.collectAsState()
            Screen(state, Store.dispatch)
        }
    }
}

@Composable
fun Screen(state: State, dispatch: Dispatch = {}) {
    ReduxTodoTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                TodoAddingForm(
                    state.todoFieldText,
                    { text -> dispatch(EditTodoFieldText(text)) },
                    { dispatch(SubmitTodoField()) }
                )
                TodoList(state.todos, dispatch)
            }
        }
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
                TodoItem(todo, onDelete = {
                    dispatch(RemoveTodo(index))
                })
            }
        }
    }
}

@Composable
fun TodoItem(todo: String, onDelete: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(text = todo, fontSize = 20.sp)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Screen(state = createMockState())
}