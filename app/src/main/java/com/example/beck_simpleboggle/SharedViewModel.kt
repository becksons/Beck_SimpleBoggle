
package com.example.beck_simpleboggle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.Serializable
import kotlin.math.max

class SharedViewModel : ViewModel() {
    val submittedWord = MutableLiveData<String>()
    val score = MutableLiveData<Int>().apply { value = 0 }
    var resetGameEvent = MutableLiveData<Boolean>()

    fun updateScore(newScore: Int) {
        score.value = max(0, (score.value ?: 0)+ newScore)
    }

    private fun resetScore() {
        score.value = 0
    }

    fun triggerGameReset() {
        resetGameEvent.value = true
        resetScore()
    }
}

