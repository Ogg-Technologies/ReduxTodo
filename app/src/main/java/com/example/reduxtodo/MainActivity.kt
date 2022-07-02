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
            ReduxTodoApp()
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
private fun ReduxTodoApp() {
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