package com.example.unimatch.data

data class OnboardingPreferences(
    val selectedSubjects: List<String> = emptyList(),
    val targetScore: Double? = null,
    val cityPreference: String? = null
)