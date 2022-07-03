package com.example.reduxtodo.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.theme.ReduxTodoTheme

@Composable
fun DetailsScreen(todo: Todo, dispatch: Dispatch = {}) {
    fun close() {
        dispatch(doScreenChangeDispatch(DetailsAction.Close))
    }
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
            }
        }
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailsPreview() {
    ReduxTodoTheme {
        DetailsScreen(Todo("Chill"))
    }
}
