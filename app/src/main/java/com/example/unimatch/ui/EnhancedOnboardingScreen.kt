package com.example.unimatch.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.unimatch.data.OnboardingPreferences
import com.example.unimatch.ui.components.*
import com.example.unimatch.viewmodel.OnboardingViewModel
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnhancedOnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedSubjects by remember { mutableStateOf(setOf<String>()) }
    var targetScore by remember { mutableStateOf("") }
    var selectedScoreType by remember { mutableStateOf("TYT") }
    var cityPreference by remember { mutableStateOf("") }
    var showPremiumDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress Indicator
        StepProgressIndicator(
            currentStep = currentStep + 1,
            totalSteps = 4,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally()).togetherWith(
                        fadeOut() + slideOutHorizontally()
                    )
                }
            ) { step ->
                when (step) {
                    0 -> WelcomeStep(
                        onPremiumClick = { showPremiumDialog = true }
                    )
                    1 -> SubjectsStep(
                        selectedSubjects = selectedSubjects,
                        onSubjectSelected = { subject ->
                            selectedSubjects = if (subject in selectedSubjects) {
                                selectedSubjects - subject
                            } else {
                                selectedSubjects + subject
                            }
                        }
                    )
                    2 -> GoalStep(
                        selectedScoreType = selectedScoreType,
                        onScoreTypeSelected = { selectedScoreType = it },
                        targetScore = targetScore,
                        onTargetScoreSelected = { targetScore = it }
                    )
                    3 -> PreferencesStep(
                        cityPreference = cityPreference,
                        onCityPreferenceChanged = { cityPreference = it }
                    )
                }
            }
        }

        // Navigation Buttons
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
                    if (currentStep < 3) {
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
                Text(if (currentStep == 3) "Get Started" else "Continue")
            }
        }
    }

    if (showPremiumDialog) {
        PremiumDialog(
            onDismiss = { showPremiumDialog = false },
            onSubscribe = { showPremiumDialog = false }
        )
    }
}

@Composable
private fun WelcomeStep(
    onPremiumClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Welcome to UniMatch",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Join 50,000+ students who found their perfect university match",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            // Statistics Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatisticCard(
                    icon = Icons.Default.Groups,
                    title = "50,000+ Students",
                    subtitle = "Active Community"
                )
                StatisticCard(
                    icon = Icons.Default.Business,
                    title = "200+ Universities",
                    subtitle = "Top Institutions"
                )
                StatisticCard(
                    icon = Icons.Default.MenuBook,
                    title = "1,000+ Programs",
                    subtitle = "Diverse Options"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Feature Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = onPremiumClick
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Unlock Premium Features",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Get personalized recommendations",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Learn more",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SubjectsStep(
    selectedSubjects: Set<String>,
    onSubjectSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "What interests you?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Select subjects you're interested in studying",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularSubjects) { subject ->
                    SubjectChip(
                        subject = subject,
                        isSelected = subject in selectedSubjects,
                        onClick = { onSubjectSelected(subject) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectChip(
    subject: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = subject,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun GoalStep(
    selectedScoreType: String,
    onScoreTypeSelected: (String) -> Unit,
    targetScore: String,
    onTargetScoreSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Set Your Goal",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "First, select your exam type",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Score Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreTypeChip(
                    text = "TYT",
                    isSelected = selectedScoreType == "TYT",
                    onClick = { onScoreTypeSelected("TYT") },
                    modifier = Modifier.weight(1f)
                )
                ScoreTypeChip(
                    text = "AYT",
                    isSelected = selectedScoreType == "AYT",
                    onClick = { onScoreTypeSelected("AYT") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What's your target score?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Score Range Selection
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val scoreRanges = if (selectedScoreType == "TYT") {
                    listOf("300-350", "350-400", "400-450", "450-500")
                } else {
                    listOf("350-400", "400-450", "450-500", "500-560")
                }

                items(scoreRanges) { range ->
                    ScoreRangeChip(
                        range = range,
                        isSelected = targetScore == range,
                        onClick = { onTargetScoreSelected(range) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "78% of students achieve their target score with regular practice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun ScoreRangeChip(
    range: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = range,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun PreferencesStep(
    cityPreference: String,
    onCityPreferenceChanged: (String) -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Almost Done!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Let's personalize your recommendations",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = cityPreference,
                    onValueChange = { },
                    label = { Text("Select City") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDropdown = true },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select city"
                        )
                    }
                )

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    turkishCities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onCityPreferenceChanged(city)
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Students who set location preferences are 2x more likely to find their match",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumDialog(
    onDismiss: () -> Unit,
    onSubscribe: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unlock Premium Features") },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Upgrade to Premium to access:")
                PremiumFeatureItem("AI-Powered Match Score")
                PremiumFeatureItem("Early Application Alerts")
                PremiumFeatureItem("Scholarship Opportunities")
                PremiumFeatureItem("Priority Support")

                Spacer(modifier = Modifier.height(8.dp))
                Text("Start your 7-day free trial today!")
            }
        },
        confirmButton = {
            Button(onClick = onSubscribe) {
                Text("Start Free Trial")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}

@Composable
private fun PremiumFeatureItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(text = text)
    }
}

private val popularSubjects = listOf(
    "Engineering",
    "Medicine",
    "Business",
    "Law",
    "Computer Science",
    "Architecture",
    "Psychology",
    "Education"
)

private val turkishCities = listOf(
    "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
    "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
    "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
    "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
    "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
    "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
    "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
    "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"
)