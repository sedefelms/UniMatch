package com.example.unimatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.example.unimatch.ui.ScoreScreen
import com.example.unimatch.ui.theme.UnimatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnimatchTheme {
                Surface(color = Color.White) {
                    ScoreScreen()
                }
            }
        }
    }
}