package com.example.unimatch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unimatch.data.ScoreData
import com.example.unimatch.viewmodel.ScoreViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder

@Composable
private fun ClickableComboBox(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        // Invisible box that covers the entire area and handles clicks
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun ScoreScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreViewModel = viewModel()
) {
    var selectedScoreType by remember { mutableStateOf("") }
    var selectedUnivType by remember { mutableStateOf("") }
    var selectedUnivName by remember { mutableStateOf("") }
    var selectedProgramName by remember { mutableStateOf("") }
    var scoreTypeExpanded by remember { mutableStateOf(false) }
    var univTypeExpanded by remember { mutableStateOf(false) }
    var univNameExpanded by remember { mutableStateOf(false) }
    var programNameExpanded by remember { mutableStateOf(false) }
    var expectedScore by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var selectedScore by remember { mutableStateOf<ScoreData?>(null) }
    var hasFilteredOnce by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(true) }

    val scoreTypes by viewModel.scoreTypes.collectAsState()
    val univTypes by viewModel.univTypes.collectAsState()
    val univNames by viewModel.univNames.collectAsState()
    val filteredScores by viewModel.filteredScores.collectAsState()
    val maxScore = remember(selectedScoreType) {
        when (selectedScoreType) {
            "TYT" -> 500.0
            else -> 560.0
        }
    }
    val rotationState by animateFloatAsState(
        targetValue = if (filtersExpanded) 180f else 0f
    )

    var filteredUnivNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var filteredProgramNames by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(scoreTypes) {
        isLoading = scoreTypes.isEmpty()
    }

    LaunchedEffect(selectedUnivType, univNames) {
        filteredUnivNames = if (selectedUnivType.isEmpty()) {
            univNames
        } else {
            viewModel.getUniversityNamesByType(selectedUnivType)
        }
    }

    LaunchedEffect(selectedScoreType, selectedUnivType, selectedUnivName) {
        if (selectedScoreType.isNotEmpty()) {
            filteredProgramNames = viewModel.getProgramNamesByFilters(
                selectedScoreType,
                selectedUnivType,
                selectedUnivName
            )
            selectedProgramName = ""
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        if (isLoading) {
            CircularProgressIndicator()
            Text("Loading...")
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { filtersExpanded = !filtersExpanded }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Filters", style = MaterialTheme.typography.titleMedium)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = if (filtersExpanded) "Collapse" else "Expand",
                            modifier = Modifier.rotate(rotationState)
                        )
                    }

                    AnimatedVisibility(
                        visible = filtersExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            ClickableComboBox(
                                value = selectedScoreType,
                                label = "Score Type",
                                onClick = { scoreTypeExpanded = true }
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
                                            selectedProgramName = ""
                                            scoreTypeExpanded = false
                                        }
                                    )
                                }
                            }
                            ClickableComboBox(
                                value = selectedUnivType,
                                label = "University Type (Optional)",
                                onClick = { univTypeExpanded = true }
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
                                        selectedUnivName = ""
                                        selectedProgramName = ""
                                        univTypeExpanded = false
                                    }
                                )
                                univTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            selectedUnivType = type
                                            selectedUnivName = ""
                                            selectedProgramName = ""
                                            univTypeExpanded = false
                                        }
                                    )
                                }
                            }
                            ClickableComboBox(
                                value = selectedUnivName,
                                label = "University Name (Optional)",
                                onClick = { univNameExpanded = true }
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
                                        selectedProgramName = ""
                                        univNameExpanded = false
                                    }
                                )
                                filteredUnivNames.forEach { name ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            selectedUnivName = name
                                            selectedProgramName = ""
                                            univNameExpanded = false
                                        }
                                    )
                                }
                            }
                            ClickableComboBox(
                                value = selectedProgramName,
                                label = "Program Name (Optional)",
                                onClick = {
                                    if (selectedScoreType.isNotEmpty()) {
                                        programNameExpanded = true
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = programNameExpanded,
                                onDismissRequest = { programNameExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All") },
                                    onClick = {
                                        selectedProgramName = ""
                                        programNameExpanded = false
                                    }
                                )
                                filteredProgramNames.forEach { name ->
                                    DropdownMenuItem(
                                        text = { Text(name) },
                                        onClick = {
                                            selectedProgramName = name
                                            programNameExpanded = false
                                        }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = expectedScore,
                                onValueChange = { newValue ->
                                    // Only update if empty or valid number within range
                                    if (newValue.isEmpty()) {
                                        expectedScore = newValue
                                    } else {
                                        newValue.toDoubleOrNull()?.let { score ->
                                            if (score in 0.0..maxScore) {
                                                expectedScore = newValue
                                            }
                                        }
                                    }
                                },
                                label = { Text("Expected Score (Optional)") },
                                placeholder = { Text("Max ${maxScore.toInt()}") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                supportingText = { Text("Leave empty or enter score (0-${maxScore.toInt()})") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            Button(
                                onClick = {
                                    val score = if (expectedScore.isEmpty()) {
                                        maxScore
                                    } else {
                                        expectedScore.toDoubleOrNull() ?: maxScore
                                    }
                                    viewModel.filterScores(selectedScoreType, selectedUnivType, selectedUnivName, selectedProgramName, score)
                                    hasFilteredOnce = true
                                    filtersExpanded = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                enabled = selectedScoreType.isNotEmpty() // Remove the selectedProgramName check
                            ) {
                                Text("List Programs")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                            onClick = { selectedScore = score },
                            isFavorite = viewModel.isFavorite(score),
                            onFavoriteClick = {
                                if (viewModel.isFavorite(score)) {
                                    viewModel.removeFromFavorites(score)
                                } else {
                                    viewModel.addToFavorites(score)
                                }
                            }
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
private fun ScoreItem(
    score: ScoreData,
    onClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            //.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(text = score.programName)
                Text(text = score.universityName)
                Text(text = "Min Score: ${score.minScore}")
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ScoreDetailsDialog(
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