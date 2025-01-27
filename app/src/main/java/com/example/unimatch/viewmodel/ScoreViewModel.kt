package com.example.unimatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimatch.data.ScoreData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val _scores = MutableStateFlow<List<ScoreData>>(emptyList())
    val scores = _scores.asStateFlow()

    private val _scoreTypes = MutableStateFlow<List<String>>(emptyList())
    val scoreTypes = _scoreTypes.asStateFlow()

    private val _univTypes = MutableStateFlow<List<String>>(emptyList())
    val univTypes = _univTypes.asStateFlow()

    private val _univNames = MutableStateFlow<List<String>>(emptyList())
    val univNames = _univNames.asStateFlow()

    private val _filteredScores = MutableStateFlow<List<ScoreData>>(emptyList())
    val filteredScores = _filteredScores.asStateFlow()

    private val _favoritePrograms = MutableStateFlow<List<ScoreData>>(emptyList())
    val favoritePrograms = _favoritePrograms.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadExcelData()
            setupFavoritesListener()
        }
    }

    private fun setupFavoritesListener() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val favoriteIds = snapshot?.documents?.mapNotNull {
                    it.getString("programKodu")
                } ?: emptyList()

                _favoritePrograms.value = _scores.value.filter {
                    it.programKodu in favoriteIds
                }
            }
    }

    private suspend fun loadExcelData() {
        try {
            val context = getApplication<Application>()
            val tytInputStream = context.assets.open("tyt.xlsx")
            val aytInputStream = context.assets.open("ayt.xlsx")

            val allScores = mutableListOf<ScoreData>()

            readExcelFile(tytInputStream).let { allScores.addAll(it) }
            readExcelFile(aytInputStream).let { allScores.addAll(it) }

            _scores.value = allScores
            _scoreTypes.value = allScores.map { it.scoreType }.distinct().sorted()
            _univTypes.value = allScores.map { it.universityType }.distinct().sorted()
            _univNames.value = allScores.map { it.universityName }.distinct().sorted()
            _filteredScores.value = emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error appropriately
        }
    }

    private fun readExcelFile(inputStream: InputStream): List<ScoreData> {
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0)
        val scores = mutableListOf<ScoreData>()

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue

            try {
                scores.add(
                    ScoreData(
                        programKodu = row.getCell(0)?.stringCellValue ?: "",
                        universityType = row.getCell(1)?.stringCellValue ?: "",
                        universityName = row.getCell(2)?.stringCellValue ?: "",
                        facultyName = row.getCell(3)?.stringCellValue ?: "",
                        programName = row.getCell(4)?.stringCellValue ?: "",
                        scoreType = row.getCell(5)?.stringCellValue ?: "",
                        quota = row.getCell(6)?.numericCellValue?.toInt() ?: 0,
                        placed = row.getCell(7)?.numericCellValue?.toInt() ?: 0,
                        minScore = row.getCell(8)?.numericCellValue ?: 0.0,
                        maxScore = row.getCell(9)?.numericCellValue ?: 0.0
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                continue
            }
        }

        workbook.close()
        inputStream.close()
        return scores
    }

    fun getUniversityNamesByType(univType: String): List<String> {
        return _scores.value
            .filter { it.universityType == univType }
            .map { it.universityName }
            .distinct()
            .sorted()
    }

    fun getProgramNamesByFilters(
        scoreType: String,
        univType: String = "",
        univName: String = ""
    ): List<String> {
        return _scores.value
            .filter { it.scoreType == scoreType }
            .filter { univType.isEmpty() || it.universityType == univType }
            .filter { univName.isEmpty() || it.universityName == univName }
            .map { it.programName }
            .distinct()
            .sorted()
    }

    fun filterScores(
        scoreType: String,
        univType: String,
        univName: String,
        programName: String,
        expectedScore: Double = 0.0
    ) {
        viewModelScope.launch {
            val originalScores = _scores.value
            _filteredScores.value = originalScores
                .filter { it.scoreType == scoreType }
                .filter { univType.isEmpty() || it.universityType == univType }
                .filter { univName.isEmpty() || it.universityName == univName }
                .filter { programName.isEmpty() || it.programName == programName }
                .filter { it.minScore <= expectedScore }
                .sortedByDescending { it.minScore }
        }
    }

    fun addToFavorites(score: ScoreData) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(score.programKodu)
                    .set(
                        mapOf(
                            "programKodu" to score.programKodu,
                            "timestamp" to System.currentTimeMillis()
                        )
                    ).await()

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error appropriately
            }
        }
    }

    fun removeFromFavorites(score: ScoreData) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch

                firestore.collection("users")
                    .document(userId)
                    .collection("favorites")
                    .document(score.programKodu)
                    .delete()
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error appropriately
            }
        }
    }

    fun isFavorite(score: ScoreData): Boolean {
        return _favoritePrograms.value.any { it.programKodu == score.programKodu }
    }
}