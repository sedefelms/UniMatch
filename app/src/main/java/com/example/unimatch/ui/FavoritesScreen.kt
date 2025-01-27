package com.example.unimatch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unimatch.data.ScoreData
import com.example.unimatch.viewmodel.ScoreViewModel

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: ScoreViewModel
) {
    val favoritePrograms by viewModel.favoritePrograms.collectAsState()
    var selectedScore by remember { mutableStateOf<ScoreData?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Favorite Programs",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoritePrograms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite programs yet",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(favoritePrograms) { score ->
                    ScoreItem(
                        score = score,
                        onClick = { selectedScore = score },
                        isFavorite = true,
                        onFavoriteClick = { viewModel.removeFromFavorites(score) }
                    )
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