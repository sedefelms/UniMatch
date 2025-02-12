package com.example.unimatch.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unimatch.viewmodel.OnboardingViewModel
import com.example.unimatch.data.OnboardingPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedSubjects by remember { mutableStateOf(setOf<String>()) }
    var targetScore by remember { mutableStateOf("") }
    var cityPreference by remember { mutableStateOf("") }

    val steps = remember {
        listOf(
            OnboardingStepData(
                title = "Welcome to UniMatch",
                description = "Join 50,000+ students who found their perfect university match last year",
                content = { WelcomeStep() }
            ),
            OnboardingStepData(
                title = "What interests you?",
                description = "Select subjects you're interested in (popular choices among students like you)",
                content = {
                    SubjectsStep(
                        selectedSubjects = selectedSubjects,
                        onSubjectSelected = { subject ->
                            selectedSubjects = if (subject in selectedSubjects) {
                                selectedSubjects - subject
                            } else {
                                selectedSubjects + subject
                            }
                        }
                    )
                }
            ),
            OnboardingStepData(
                title = "Set Your Goal",
                description = "95% of successful matches start with a clear target score",
                content = {
                    GoalStep(
                        targetScore = targetScore,
                        onTargetScoreChanged = { targetScore = it }
                    )
                }
            ),
            OnboardingStepData(
                title = "Almost Done!",
                description = "Let's personalize your recommendations",
                content = {
                    PreferencesStep(
                        cityPreference = cityPreference,
                        onCityPreferenceChanged = { cityPreference = it }
                    )
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            steps.indices.forEach { index ->
                LinearProgressIndicator(
                    progress = if (index <= currentStep) 1f else 0f,
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Step content
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = steps[currentStep].title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = steps[currentStep].description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            steps[currentStep].content()
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { currentStep-- },
                enabled = currentStep > 0
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    if (currentStep < steps.lastIndex) {
                        currentStep++
                    } else {
                        viewModel.updatePreferences(
                            OnboardingPreferences(
                                selectedSubjects = selectedSubjects.toList(),
                                targetScore = targetScore.toDoubleOrNull(),
                                cityPreference = cityPreference.takeIf { it.isNotBlank() }
                            )
                        )
                        onFinished()
                    }
                }
            ) {
                Text(if (currentStep == steps.lastIndex) "Get Started" else "Continue")
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        StatisticRow(
            icon = Icons.Default.Groups,
            text = "50,000+ active students"
        )

        StatisticRow(
            icon = Icons.Default.Search,
            text = "200+ universities"
        )

        StatisticRow(
            icon = Icons.Default.MenuBook,
            text = "1,000+ programs"
        )
    }
}

@Composable
private fun StatisticRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SubjectsStep(
    selectedSubjects: Set<String>,
    onSubjectSelected: (String) -> Unit
) {
    val popularSubjects = remember {
        listOf(
            "Engineering",
            "Medicine",
            "Business",
            "Law",
            "Computer Science",
            "Architecture"
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(popularSubjects) { subject ->
            val isSelected = subject in selectedSubjects
            OutlinedButton(
                onClick = { onSubjectSelected(subject) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = subject,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

@Composable
private fun GoalStep(
    targetScore: String,
    onTargetScoreChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = targetScore,
            onValueChange = { value ->
                if (value.isEmpty() || value.toDoubleOrNull() != null) {
                    onTargetScoreChanged(value)
                }
            },
            label = { Text("Target Score") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "78% of students achieve their target score with regular practice",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun PreferencesStep(
    cityPreference: String,
    onCityPreferenceChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = cityPreference,
            onValueChange = onCityPreferenceChanged,
            label = { Text("Preferred City (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Students who set location preferences are 2x more likely to find their match",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}