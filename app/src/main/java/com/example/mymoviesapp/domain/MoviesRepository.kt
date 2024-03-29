package com.example.mymoviesapp.domain

import com.example.mymoviesapp.domain.models.GenreMovie
import com.example.mymoviesapp.domain.models.GuestSession
import com.example.mymoviesapp.domain.models.MovieModel
import com.example.mymoviesapp.domain.models.RatingResponse

interface MoviesRepository {

    suspend fun getAllPopularMovies(): List<MovieModel>

    suspend fun getMoviesSearched(searchMovieTitle: String): List<MovieModel>

    suspend fun getGenresMovies() : List<GenreMovie>

    suspend fun getAllFavouriteMovies(): List<MovieModel>

    suspend fun updateMovie(movieModel: MovieModel)

    suspend fun getMovieDatabase(id: Int) : MovieModel

    suspend fun getSelectedGenres(): List<GenreMovie>

    suspend fun insertGenres(genres: List<GenreMovie>)

    suspend fun getSessionId(): GuestSession

    suspend fun ratingMovie(movie: MovieModel, valuation: Double, guessSessionId: String): RatingResponse
}