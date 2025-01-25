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
    var selectedUnivName by remember { mutableStateOf("") }
    var scoreTypeExpanded by remember { mutableStateOf(false) }
    var univTypeExpanded by remember { mutableStateOf(false) }
    var univNameExpanded by remember { mutableStateOf(false) }
    var expectedScore by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var selectedScore by remember { mutableStateOf<ScoreData?>(null) }
    var hasFilteredOnce by remember { mutableStateOf(false) }
    val scoreTypes by viewModel.scoreTypes.collectAsState()
    val univTypes by viewModel.univTypes.collectAsState()
    val univNames by viewModel.univNames.collectAsState()
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
                value = selectedUnivName,
                onValueChange = { },
                readOnly = true,
                label = { Text("University Name (Optional)") },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .clickable { univNameExpanded = true }
            )

            DropdownMenu(
                expanded = univNameExpanded,
                onDismissRequest = { univNameExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        selectedUnivName = ""
                        univNameExpanded = false
                    }
                )
                univNames.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            selectedUnivName = name
                            univNameExpanded = false
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
                    viewModel.filterScores(selectedScoreType, selectedUnivType, selectedUnivName, score)
                    hasFilteredOnce = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("List Programs")
            }

            LazyColumn {
                if (hasFilteredOnce && filteredScores.isEmpty()) {
                    item {
                        Text(
                            text = "No programs match your criteria",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(filteredScores) { score ->
                        ScoreItem(
                            score = score,
                            onClick = { selectedScore = score }
                        )
                    }
                }
            }
        }
    }

    if (selectedScore != null) {
        ScoreDetailsDialog(
            score = selectedScore!!,
            onDismiss = { selectedScore = null }
        )
    }
}

@Composable
fun ScoreItem(
    score: ScoreData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = score.programName)
            Text(text = score.universityName)
            Text(text = "Min Score: ${score.minScore}")
        }
    }
}

@Composable
fun ScoreDetailsDialog(
    score: ScoreData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(score.programName) },
        text = {
            Column {
                Text("Program Code: ${score.programKodu}")
                Text("University Type: ${score.universityType}")
                Text("University: ${score.universityName}")
                Text("Faculty: ${score.facultyName}")
                Text("Score Type: ${score.scoreType}")
                Text("Quota: ${score.quota}")
                Text("Placed: ${score.placed}")
                Text("Min Score: ${score.minScore}")
                Text("Max Score: ${score.maxScore}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}