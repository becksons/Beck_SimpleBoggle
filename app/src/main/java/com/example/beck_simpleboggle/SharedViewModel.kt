
package com.example.beck_simpleboggle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val submittedWord = MutableLiveData<String>()
    val score = MutableLiveData<Int>()
    var resetGameEvent = MutableLiveData<Boolean>()

    fun newGame() {
        resetGameEvent.value = true
    }

    fun triggerGameReset() {
        resetGameEvent.value = true
    }

    fun onGameResetHandled() {
        resetGameEvent.value = false
    }
}
