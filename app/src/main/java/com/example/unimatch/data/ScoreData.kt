package com.example.unimatch.data

data class ScoreData(
    val programKodu: String,
    val universityType: String,
    val universityName: String,
    val facultyName: String,
    val programName: String,
    val scoreType: String,
    val quota: Int,
    val placed: Int,
    val minScore: Double,
    val maxScore: Double
)
