package com.example.reduxtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state: State by Store.stateFlow.collectAsState()
            Screen(state)
        }
    }
}

@Composable
fun Screen(state: State) {
    ReduxTodoTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                TodoAddingForm(
                    state.todoFieldText,
                    { text -> Store.dispatch(EditTodoFieldText(text)) },
                    { Store.dispatch(SubmitTodoField()) }
                )
                TodoList(state.todos)
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
fun TodoList(todos: List<String>) {
    LazyColumn {
        todos.forEach { todo ->
            item { TodoItem(todo) }
        }
    }
}

@Composable
fun TodoItem(todo: String) {
    Text(text = todo)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Screen(state = createMockState())
}