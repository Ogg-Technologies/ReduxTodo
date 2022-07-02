package com.example.reduxtodo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.reduxtodo.model.Details
import com.example.reduxtodo.model.Dispatch

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


@Preview(showBackground = true)
@Composable
fun DetailsPreview() {
    DetailsScreen("Chill", {})
}
