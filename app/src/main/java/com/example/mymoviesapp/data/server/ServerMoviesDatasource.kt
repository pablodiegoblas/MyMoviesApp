package com.example.mymoviesapp.data.server

import com.example.mymoviesapp.data.api.APIServices.MoviesService
import com.example.mymoviesapp.data.mappers.toDomainModel
import com.example.mymoviesapp.domain.models.GenreMovie
import com.example.mymoviesapp.domain.models.GuestSession
import com.example.mymoviesapp.domain.models.MovieModel
import com.example.mymoviesapp.domain.models.RatingResponse
import javax.inject.Inject

class ServerMoviesDatasource @Inject constructor(
    private val moviesService: MoviesService
) {

    /**
     * Function that returns all popular movies
     */
    suspend fun getAllPopularMoviesRequest(): List<MovieModel> {
        return moviesService.getPopularMovies().results.map { it.toDomainModel() }
    }

    /**
     * Function that search movie by title in SearchBar for popular fragment
     */
    suspend fun getMoviesSearched(
        searchMovieTitle: String
    ): List<MovieModel> {
        return moviesService.getSearchMovie(searchMovieTitle).results.map { it.toDomainModel() }
    }

    suspend fun getGenreMovies(): List<GenreMovie> {
        return moviesService.getGenresMoviesList().toDomainModel().genres
    }

    suspend fun getGuestSessionId(): GuestSession =
        moviesService.guestSessionNew().toDomainModel()

    suspend fun ratingMovie(movie: Int, apiKey: String, valuation: Double, guestSessionId: String): RatingResponse =
        moviesService.ratingMovie(
            movieId = movie,
            api_key = apiKey,
            value = ValuationValue(valuation),
            guestSessionId = guestSessionId
        ).toDomainModel()
}