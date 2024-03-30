package com.example.beck_simpleboggle
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.gameboard_frag_container, GameBoardFragment())
                replace(R.id.score_frag_container, ScoreFragment())
            }
        }

    }
}
