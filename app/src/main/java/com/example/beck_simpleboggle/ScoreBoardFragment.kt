package com.example.beck_simpleboggle
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.io.IOException
import kotlin.math.max


class ScoreFragment : Fragment() {


    private var isValid = false
    private lateinit var viewModel: SharedViewModel
    private var newGameButton: Button? = null
    private var scoreCount: TextView? = null
    private var seenWords = mutableListOf<String>()
    private var seenChars = mutableListOf<Char>()
    private var newGameSelected  = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.score_fragment, container, false)
    }

//Asked chatGPT and used documentation on how to access and parse a txt file
    private fun isWordInDictionary(context: Context, word: String): Boolean {
        try {
            context.assets.open("dictionary.txt").use { inputStream ->
                inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        if (word.equals(line.trim(), ignoreCase = true)) {
                            Log.d("Dictionary Check","Valid Word: $word")
                            return true

                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.d(tag,"Unable to access dictionary")
            e.printStackTrace()
        }
        return false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        newGameButton = view.findViewById(R.id.new_game_button)
        scoreCount = view.findViewById(R.id.score_count)
        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            scoreCount?.text = newScore.toString()
        }

        viewModel.submittedWord.observe(viewLifecycleOwner) { word ->
            Log.d("ScoreFragment", "Submitted word: $word")
            if(!seenWords.contains(word) and (word!= "")){

                seenWords.add(word)
                if (isWordInDictionary(requireContext(), word)) {
                    isValid = true
                    calculateAndUpdateScore(word)
                }else{
                    Log.d("Dictionary Check","Word $word not valid")
                    activity?.runOnUiThread {
                        Toast.makeText(
                            view.context,
                            "The word $word is not a valid word",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else{
                if(word.equals("")){
                    activity?.runOnUiThread {
                        Toast.makeText(view.context, "Submit a word", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    activity?.runOnUiThread {
                        Toast.makeText(view.context, "Word already used", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
        newGameButton?.setOnClickListener {
            viewModel.triggerGameReset()
            viewModel.score.value = 0

        }
    }

    private fun calculateAndUpdateScore(word: String) {
        val (calculatedScore, hasEnoughVowels) = calculateScore(word)
        var scoreChange = 0
        val currentScore = viewModel.score.value


        if (!hasEnoughVowels) {
            activity?.runOnUiThread {
                Toast.makeText(context, "Word must have at least 2 vowels", Toast.LENGTH_SHORT).show()
            }
            viewModel.updateScore(-10)
            scoreChange = -10
            activity?.runOnUiThread {
                Toast.makeText(context, " $scoreChange \n Score:$calculatedScore", Toast.LENGTH_SHORT).show()
            }
        } else {
            scoreChange = calculatedScore
            val updatedScore = max(scoreChange + currentScore!!, 0)
            scoreChange = calculatedScore


            activity?.runOnUiThread {
                Toast.makeText(context, " +$scoreChange \n Score:$updatedScore", Toast.LENGTH_SHORT).show()
            }
            viewModel.updateScore(calculatedScore)
        }
    }

    private fun calculateScore(word: String): Pair<Int, Boolean> {
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val specialConsonants = listOf('S', 'Z', 'P', 'X', 'Q')
        val vowelCount = word.count { it.uppercaseChar() in vowels }
        if(word.length>=4){
            var wordScore = word.uppercase().fold(0) { acc, c ->
                when {
                    vowels.contains(c) -> acc + 5
                    else -> acc + 1
                }
            }
            if (word.any { it.uppercaseChar() in specialConsonants }) wordScore *= 2


            return Pair(wordScore, vowelCount >= 2)

        }else{
            val scoreDed = viewModel.score.value!! -10
            activity?.runOnUiThread {
                Toast.makeText(context, "Word must be at least 4 characters", Toast.LENGTH_SHORT).show()
            }

            return Pair(scoreDed,vowelCount >= 2)

        }


    }

}


