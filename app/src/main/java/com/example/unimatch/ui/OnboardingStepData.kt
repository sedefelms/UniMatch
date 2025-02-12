package com.example.unimatch.ui

import androidx.compose.runtime.Composable

data class OnboardingStepData(
    val title: String,
    val description: String,
    val content: @Composable () -> Unit
)