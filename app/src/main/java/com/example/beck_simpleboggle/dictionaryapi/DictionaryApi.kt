package com.example.beck_simpleboggle.dictionaryapi


import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("https://wordsapiv1.p.rapidapi.com/words/{word}")
    fun checkWord(@Path("word") word: String): retrofit2.Call<WordResponse>
}
