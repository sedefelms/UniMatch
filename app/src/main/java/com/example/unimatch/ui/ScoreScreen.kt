package com.example.unimatch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unimatch.data.ScoreData
import com.example.unimatch.viewmodel.ScoreViewModel

@Composable
fun ScoreScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreViewModel = viewModel()
) {
    var selectedScoreType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var minScore by remember { mutableStateOf("") }
    var maxScore by remember { mutableStateOf("") }
    val scoreTypes by viewModel.scoreTypes.collectAsState()
    val filteredScores by viewModel.filteredScores.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = selectedScoreType,
            onValueChange = { },
            readOnly = true,
            label = { Text("Score Type") },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            scoreTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        selectedScoreType = type
                        expanded = false
                    }
                )
            }
        }

        OutlinedTextField(
            value = minScore,
            onValueChange = { minScore = it },
            label = { Text("Minimum Score") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = maxScore,
            onValueChange = { maxScore = it },
            label = { Text("Maximum Score") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Button(
            onClick = {
                val min = minScore.toDoubleOrNull() ?: 0.0
                val max = maxScore.toDoubleOrNull() ?: Double.MAX_VALUE
                viewModel.filterScores(selectedScoreType, min, max)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("List Programs")
        }

        LazyColumn {
            items(filteredScores) { score ->
                ScoreItem(score)
            }
        }
    }
}

@Composable
fun ScoreItem(score: ScoreData) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "Program: ${score.programName}")
        Text(text = "University: ${score.universityName}")
        Text(text = "Faculty: ${score.facultyName}")
        Text(text = "Min Score: ${score.minScore}")
        Text(text = "Max Score: ${score.maxScore}")
    }
}