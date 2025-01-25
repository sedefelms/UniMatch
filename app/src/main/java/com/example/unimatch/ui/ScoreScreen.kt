package com.example.unimatch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var selectedUnivType by remember { mutableStateOf("") }
    var scoreTypeExpanded by remember { mutableStateOf(false) }
    var univTypeExpanded by remember { mutableStateOf(false) }
    var expectedScore by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scoreTypes by viewModel.scoreTypes.collectAsState()
    val univTypes by viewModel.univTypes.collectAsState()
    val filteredScores by viewModel.filteredScores.collectAsState()

    LaunchedEffect(scoreTypes) {
        isLoading = scoreTypes.isEmpty()
    }

    Column(modifier = modifier.padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                "Loading score types...",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            OutlinedTextField(
                value = selectedScoreType,
                onValueChange = { },
                readOnly = true,
                label = { Text("Score Type") },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .clickable { scoreTypeExpanded = true }
            )

            DropdownMenu(
                expanded = scoreTypeExpanded,
                onDismissRequest = { scoreTypeExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                scoreTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedScoreType = type
                            scoreTypeExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = selectedUnivType,
                onValueChange = { },
                readOnly = true,
                label = { Text("University Type (Optional)") },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .clickable { univTypeExpanded = true }
            )

            DropdownMenu(
                expanded = univTypeExpanded,
                onDismissRequest = { univTypeExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        selectedUnivType = ""
                        univTypeExpanded = false
                    }
                )
                univTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedUnivType = type
                            univTypeExpanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = expectedScore,
                onValueChange = { expectedScore = it },
                label = { Text("Expected Score") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Button(
                onClick = {
                    val score = expectedScore.toDoubleOrNull() ?: 0.0
                    viewModel.filterScores(selectedScoreType, selectedUnivType, score)
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