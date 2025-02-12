package com.example.unimatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimatch.data.PreferencesManager
import com.example.unimatch.data.OnboardingPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    val hasCompletedOnboarding: StateFlow<Boolean> = preferencesManager.hasCompletedOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun completeOnboarding() {
        viewModelScope.launch {
            // Save completion status locally
            preferencesManager.setOnboardingCompleted()

            // Save to Firestore that onboarding is completed
            auth.currentUser?.uid?.let { userId ->
                try {
                    firestore.collection("users")
                        .document(userId)
                        .update(mapOf(
                            "hasCompletedOnboarding" to true,
                            "lastUpdated" to System.currentTimeMillis()
                        )).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updatePreferences(preferences: OnboardingPreferences) {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { userId ->
                try {
                    firestore.collection("users")
                        .document(userId)
                        .update(mapOf(
                            "onboardingPreferences" to mapOf(
                                "selectedSubjects" to preferences.selectedSubjects,
                                "targetScore" to preferences.targetScore,
                                "cityPreference" to preferences.cityPreference
                            ),
                            "hasCompletedOnboarding" to true,
                            "lastUpdated" to System.currentTimeMillis()
                        )).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Also mark onboarding as completed locally
            preferencesManager.setOnboardingCompleted()
        }
    }
}