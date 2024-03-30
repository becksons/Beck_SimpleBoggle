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

    private var score = 0
    private var isValid = false
    private lateinit var viewModel: SharedViewModel
    private var newGameButton: Button? = null
    private var scoreCount: TextView? = null
    private var seenWords = mutableListOf<String>()
    private var newGameSelected  = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.score_fragment, container, false)
    }
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



        viewModel.submittedWord.observe(viewLifecycleOwner) { word ->
            Log.d("ScoreFragment", "Submitted word: $word")
            if(!seenWords.contains(word)){
                seenWords.add(word)
                if (isWordInDictionary(requireContext(), word)) {
                    isValid = true
                    calculateAndUpdateScore(word)
                }else{
                    Log.d("Dictionary Check","Word $word not valid")
                    Toast.makeText(view.context,"The word $word is not a valid word",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(view.context,"Word already used",Toast.LENGTH_SHORT).show()
            }


        }


        newGameButton?.setOnClickListener {
            viewModel.triggerGameReset()


        }
        if(newGameSelected){
            score = 0

        }

            viewModel.resetGameEvent.observe(viewLifecycleOwner) { reset ->
                if (reset) {
                    Log.d("Reset Game","Game reset called")
                    resetGame()

                }
            }





    }

    private fun calculateAndUpdateScore(word: String) {
        if(word.length<4){
            Log.d("Word length deduct", "Word len less than 4")
            score -= 10
            score = max(score,0)
            Toast.makeText(view?.context,"Words must be at least 4 letters: -10",Toast.LENGTH_SHORT).show()

        }else{
            val scoreChange = calculateScore(word)


            score += scoreChange
            Toast.makeText(view?.context,"+$scoreChange !",Toast.LENGTH_SHORT).show()

        }
        if(newGameSelected){
            view?.findViewById<TextView>(R.id.score_count)?.text ="0"

        }else{
            view?.findViewById<TextView>(R.id.score_count)?.text = score.toString()


        }


    }
    private fun resetGame() {
        Log.d("Reset Game", "Resetting game...")
        score = 0
        view?.findViewById<TextView>(R.id.score_count)?.text = score.toString()

    }
    private fun calculateScore(word: String): Int {
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val specialConsonants = listOf('S', 'Z', 'P', 'X', 'Q')
        var wordScore = word.uppercase().fold(0) { acc, c ->
            when {

                vowels.contains(c) -> acc + 5



                else -> acc + 1
            }
        }
        if (word.any { it.uppercaseChar() in specialConsonants }) wordScore *= 2

        return wordScore
    }



    companion object {
      fun newInstance() = ScoreFragment()
    }
}


