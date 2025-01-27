package com.example.unimatch.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val _isAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    fun signIn(email: String, password: String, onResult: (String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult("Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isAuthenticated.value = true
                    onResult(null)
                } else {
                    onResult(task.exception?.message ?: "Authentication failed")
                }
            }
    }

    fun signUp(email: String, password: String, onResult: (String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult("Email and password cannot be empty")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isAuthenticated.value = true
                    onResult(null)
                } else {
                    onResult(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _isAuthenticated.value = false
    }
}