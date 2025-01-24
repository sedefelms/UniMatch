package com.example.unimatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimatch.data.ScoreData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val _scores = MutableStateFlow<List<ScoreData>>(emptyList())
    val scores = _scores.asStateFlow()

    private val _scoreTypes = MutableStateFlow<List<String>>(emptyList())
    val scoreTypes = _scoreTypes.asStateFlow()

    private val _filteredScores = MutableStateFlow<List<ScoreData>>(emptyList())
    val filteredScores = _filteredScores.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadExcelData()
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
            _scoreTypes.value = allScores.map { it.scoreType }.distinct()
            _filteredScores.value = allScores
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun loadScoreTypes(): List<String> {
        return _scoreTypes.value
    }

    fun filterScores(scoreType: String) {
        viewModelScope.launch {
            val originalScores = _scores.value
            _filteredScores.value = originalScores.filter { it.scoreType == scoreType }
        }
    }
}