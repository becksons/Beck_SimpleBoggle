package com.example.beck_simpleboggle


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class GameBoardFragment : Fragment() {



    private lateinit var buttons: Array<Array<Button>>
    private var firstSelection = true
    private var lastSelectedRow: Int = -1
    private var lastSelectedCol: Int = -1
    private var wordDisplay : TextView?  = null
    private var wordBuilder = StringBuilder()
    private var clearButton :Button?  = null
    private var submitButton: Button? = null
    private lateinit var viewModel: SharedViewModel




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.gameboard_fragment, container, false)
        initializeGameBoard(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordDisplay = view.findViewById(R.id.word_display)
        clearButton = view.findViewById(R.id.clear_button)
        submitButton = view.findViewById(R.id.submit_button)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        viewModel.resetGameEvent.observe(viewLifecycleOwner) { reset ->
            if (reset) {
                viewModel.resetGameEvent.value = false
                resetGame()

            }
        }

        clearButton?.setOnClickListener {
            clearBoard()
        }

        submitButton?.setOnClickListener {
            if(wordBuilder.toString() == ""){
                wordDisplay?.text = ""

            }

            val word = wordDisplay?.text.toString()
            viewModel.submittedWord.value = word
            clearBoard()
            Log.d("GameBoardFragment", "Word Submitted: $word")

        }
    }

    private fun resetGame() {
        Log.d("GameBoardFragment", "Game resetting...")
        wordBuilder.clear()
        wordDisplay?.text = getString(R.string.word)
        firstSelection = true
        buttons.forEach { row-> row.forEach { col->
            col.isEnabled= true
        } }
        viewModel.resetGameEvent
        initializeGameBoard(requireView())
        viewModel.resetGameEvent.value = false
    }
    private fun clearBoard(){
        wordDisplay?.text = ""
        wordBuilder.clear()
        buttons.forEach { row->
            row.forEach {col->
                col.isEnabled = true
                firstSelection = true
            }
        }
    }
    private fun initializeGameBoard(view: View) {
        //Asked ChatGPT for help with list comprehension and syntax for getting random letters
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val consonants = ('A'..'Z').toList() - vowels
        val totalLetters = 16
        val numberOfVowels = 2

        val lettersForBoard = mutableListOf<Char>().apply {
            repeat(numberOfVowels) {
                add(vowels.random())
            }
            repeat(totalLetters - numberOfVowels) {
                add((vowels + consonants).random())
            }
        }.shuffled()


        val buttonIds = arrayOf(
            arrayOf(R.id.Button00, R.id.Button01, R.id.Button02, R.id.Button03),
            arrayOf(R.id.Button10, R.id.Button11, R.id.Button12, R.id.Button13),
            arrayOf(R.id.Button20, R.id.Button21, R.id.Button22, R.id.Button23),
            arrayOf(R.id.Button30, R.id.Button31, R.id.Button32, R.id.Button33)
        )

        buttons = Array(4) { row ->
            Array(4) { col ->
                view.findViewById<Button>(buttonIds[row][col]).apply {
                    val letterIndex = row * 4 + col
                    text = lettersForBoard[letterIndex].toString()
                    setOnClickListener {
                        onButtonSelected(row, col, it as Button)
                    }
                }
            }
        }
    }


    private fun onButtonSelected(row: Int, col: Int, button: Button) {
        if (firstSelection || isAdjacent(row, col) ) {
            button.isEnabled = false
            firstSelection = false
            lastSelectedRow = row
            lastSelectedCol = col
            wordBuilder.append(button.text.toString())
            wordDisplay?.text = wordBuilder.toString()


        }
    }

    private fun isAdjacent(row: Int, col: Int): Boolean {
        return (row in (lastSelectedRow - 1)..(lastSelectedRow + 1)) && (col in (lastSelectedCol - 1)..(lastSelectedCol + 1))
    }
}
