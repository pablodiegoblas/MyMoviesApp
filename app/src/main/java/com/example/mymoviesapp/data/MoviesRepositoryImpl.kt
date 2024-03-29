package com.example.mymoviesapp.data

import com.example.mymoviesapp.data.api.interceptors.ApiKeyQuery
import com.example.mymoviesapp.data.database.MoviesDbDataSource
import com.example.mymoviesapp.data.mappers.mapGenresSelected
import com.example.mymoviesapp.data.mappers.mapListFavourites
import com.example.mymoviesapp.data.server.ServerMoviesDatasource
import com.example.mymoviesapp.domain.MoviesRepository
import com.example.mymoviesapp.domain.models.GenreMovie
import com.example.mymoviesapp.domain.models.GuestSession
import com.example.mymoviesapp.domain.models.MovieModel
import com.example.mymoviesapp.domain.models.RatingResponse
import com.example.mymoviesapp.extension.sortedByGenres
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val serverMoviesProvider: ServerMoviesDatasource,
    private val dbDataSource: MoviesDbDataSource
) : MoviesRepository {

    /**
     * Return the movies with favourites items
     */
    override suspend fun getAllPopularMovies(): List<MovieModel> {
        val apiPopularMovies = serverMoviesProvider.getAllPopularMoviesRequest()

        dbDataSource.insertAll(apiPopularMovies)

        val listWithFavorites = dbDataSource.getAllFavouritesMovies()
        val genresFavorites = dbDataSource.getSelectedGenres()

        return apiPopularMovies.mapListFavourites(listWithFavorites).sortedByGenres(genresFavorites.map { it.id })
    }

    override suspend fun getMoviesSearched(searchMovieTitle: String): List<MovieModel> {
        val searchedMovies = serverMoviesProvider.getMoviesSearched(searchMovieTitle)

        dbDataSource.insertAll(searchedMovies)

        val listWithFavorites = dbDataSource.getAllFavouritesMovies()
        return searchedMovies.mapListFavourites(listWithFavorites)
    }

    override suspend fun getGenresMovies(): List<GenreMovie> {
        val selectedGenres = dbDataSource.getSelectedGenres()

        return serverMoviesProvider.getGenreMovies().mapGenresSelected(selectedGenres)
    }

    override suspend fun getAllFavouriteMovies(): List<MovieModel> {
        return dbDataSource.getAllFavouritesMovies()
    }

    override suspend fun updateMovie(movieModel: MovieModel) {
        dbDataSource.updateMovie(movieModel)
    }

    override suspend fun getMovieDatabase(id: Int): MovieModel {
        return dbDataSource.findMovie(id)
    }

    override suspend fun getSelectedGenres(): List<GenreMovie> {
        return dbDataSource.getSelectedGenres()
    }

    override suspend fun insertGenres(genres: List<GenreMovie>) {
        dbDataSource.deleteGenres()
        dbDataSource.insertSelectedGenres(genres = genres)
    }

    override suspend fun getSessionId(): GuestSession =
        serverMoviesProvider.getGuestSessionId()

    override suspend fun ratingMovie(movie: MovieModel, valuation: Double, guestSessionId: String): RatingResponse {
        val response = serverMoviesProvider.ratingMovie(movie.id, ApiKeyQuery.fromApiKey().value, valuation, guestSessionId)

        if (response.success) {
            dbDataSource.updateMovie(movieModel = movie.copy(personalValuation = valuation))
        }

        return response
    }
}