package com.example.unimatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unimatch.ui.AuthScreen
import com.example.unimatch.ui.FavoritesScreen
import com.example.unimatch.ui.EnhancedOnboardingScreen
import com.example.unimatch.ui.ScoreScreen
import com.example.unimatch.ui.theme.UnimatchTheme
import com.example.unimatch.viewmodel.AuthViewModel
import com.example.unimatch.viewmodel.OnboardingViewModel
import com.example.unimatch.viewmodel.ScoreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnimatchTheme {
                UnimatchApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnimatchApp() {
    val navController = rememberNavController()
    val scoreViewModel: ScoreViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val onboardingViewModel: OnboardingViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isNewUser by authViewModel.isNewUser.collectAsState()
    val hasCompletedOnboarding by onboardingViewModel.hasCompletedOnboarding.collectAsState()
    val currentRoute by navController.currentBackStackEntryAsState()

    val items = listOf(
        NavigationItem("search", "Search", Icons.Default.Search),
        NavigationItem("favorites", "Favorites", Icons.Default.Favorite)
    )

    LaunchedEffect(isAuthenticated, isNewUser, hasCompletedOnboarding) {
        when {
            !isAuthenticated -> {
                scoreViewModel.clearUserData()
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            }
            isNewUser || !hasCompletedOnboarding -> {
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                }
            }
            currentRoute?.destination?.route == "auth" || currentRoute?.destination?.route == "onboarding" -> {
                navController.navigate("search") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (isAuthenticated &&
                currentRoute?.destination?.route != "auth" &&
                currentRoute?.destination?.route != "onboarding"
            ) {
                TopAppBar(
                    title = { Text("UniMatch") },
                    actions = {
                        IconButton(onClick = {
                            authViewModel.signOut()
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.ExitToApp,
                                contentDescription = "Log out"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isAuthenticated &&
                currentRoute?.destination?.route != "auth" &&
                currentRoute?.destination?.route != "onboarding"
            ) {
                NavigationBar {
                    val currentDestination = currentRoute?.destination?.route
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination == item.route,
                            onClick = {
                                if (currentDestination != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("onboarding") {
                EnhancedOnboardingScreen(
                    onFinished = {
                        onboardingViewModel.completeOnboarding()
                        authViewModel.clearNewUserFlag()
                        navController.navigate("search") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                    viewModel = onboardingViewModel
                )
            }

            composable("auth") {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        if (isNewUser || !hasCompletedOnboarding) {
                            navController.navigate("onboarding") {
                                popUpTo("auth") { inclusive = true }
                            }
                        } else {
                            navController.navigate("search") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable("search") {
                if (isAuthenticated) {
                    ScoreScreen(viewModel = scoreViewModel)
                }
            }

            composable("favorites") {
                if (isAuthenticated) {
                    FavoritesScreen(viewModel = scoreViewModel)
                }
            }
        }
    }
}

private data class NavigationItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)