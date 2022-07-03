package com.example.reduxtodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.reduxtodo.model.*
import com.example.reduxtodo.ui.screens.DetailsScreen
import com.example.reduxtodo.ui.screens.MainScreen
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
                DetailsScreen(state, Store.dispatch)
            } else {
                MainScreen(state, Store.dispatch)
            }
        }
    }
}