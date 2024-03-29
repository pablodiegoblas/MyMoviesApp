package com.example.mymoviesapp.data.api.APIServices

import com.example.mymoviesapp.data.server.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesService {

    @GET("movie/popular")
    suspend fun getPopularMovies(): ResponseModel

    @GET("search/movie")
    suspend fun getSearchMovie(@Query("query") query: String): ResponseModel

    @GET("genre/movie/list")
    suspend fun getGenresMoviesList(): ApiGenresMovies

    @GET("authentication/guest_session/new")
    suspend fun guestSessionNew(): ApiGuestSession

    @POST("movie/{movieId}/rating")
    suspend fun ratingMovie(
        @Path("movieId") movieId: Int,
        @Query("api_key") api_key: String,
        @Query("guest_session_id") guestSessionId: String,
        @Body value: ValuationValue
    ): ApiRatingResponse
}