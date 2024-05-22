package com.example.szlaki

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Stopwatch {
    private var isRunning = mutableStateOf(false)
    private var elapsedSeconds = mutableIntStateOf(0)
    private var job: Job? = null

    var currentIcon = mutableStateOf(Icons.Default.PlayArrow)


    fun toggle() {
        if(isRunning.value) {
            isRunning.value = false
            currentIcon.value = Icons.Default.PlayArrow
            job?.cancel()
        } else {
            isRunning.value = true
            currentIcon.value = Icons.Default.Pause
            job = CoroutineScope(Dispatchers.Main).launch {
                while(isRunning.value) {
                    delay(1000)
                    elapsedSeconds.intValue += 1
                }
            }
        }
    }

    fun reset() {
        isRunning.value = false
        currentIcon.value = Icons.Default.PlayArrow
        job?.cancel()
        elapsedSeconds.intValue = 0
    }

    fun getString(): String {
        val minutes = (elapsedSeconds.intValue / 60)
        return String.format("%d:%02d:%02d", minutes/60, minutes%60, elapsedSeconds.intValue%60)
    }
}

@Composable
fun StopwatchPanel(stopwatch: Stopwatch) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
        IconButton(onClick = { stopwatch.toggle() }, modifier = Modifier.size(50.dp)) {
            Icon(stopwatch.currentIcon.value, contentDescription = "Play")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(stopwatch.getString(), style = MaterialTheme.typography.titleLarge )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { stopwatch.reset() }, modifier = Modifier.size(50.dp)) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset")
        }
    }
}