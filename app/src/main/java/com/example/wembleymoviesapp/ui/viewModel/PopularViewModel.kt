package com.example.wembleymoviesapp.ui.viewModel

import android.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wembleymoviesapp.data.database.DBMoviesProvider
import com.example.wembleymoviesapp.data.database.MovieDB
import com.example.wembleymoviesapp.data.mappers.ServerDataMapper
import com.example.wembleymoviesapp.data.server.ResponseModel
import com.example.wembleymoviesapp.data.server.ServerMoviesProvider
import com.example.wembleymoviesapp.domain.MovieItem
import com.example.wembleymoviesapp.domain.GetMoviesServer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor() : ViewModel(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener,
    SearchView.OnCloseListener {

    @Inject lateinit var dbProvider: DBMoviesProvider
    @Inject lateinit var serverMoviesProvider: ServerMoviesProvider
    @Inject lateinit var dataMapperServer: ServerDataMapper

    val popularMovieModel = MutableLiveData<List<MovieItem>?>()

    fun returnAllPopularMovies() {
        serverMoviesProvider.getAllPopularMoviesRequest(object : GetMoviesServer {
            override fun onSuccess(responseServer: ResponseModel) {
                val popularMoviesDBModel = dataMapperServer.convertListToMovieDB(responseServer)
                // Insert in DB all movies that returns server
                insertMoviesInDatabase(popularMoviesDBModel)

                val listMovieItems =
                    dataMapperServer.convertListToDomainMovieItem(responseServer)
                val newList = mapListWithFavourites(listMovieItems)

                popularMovieModel.postValue(newList)
            }

            override fun onError() {
                popularMovieModel.postValue(emptyList())
            }

        })
    }

    fun createDB() = dbProvider.openDB()
    fun destroyDB() = dbProvider.closeDatabase()

    fun mapListWithFavourites(listMovieItems: List<MovieItem>): List<MovieItem> {
         return listMovieItems.mapIndexed { _, movieItem ->
            if (dbProvider.findMovie(movieItem.id)?.favourite == true) movieItem.copy(favourite = true)
            else movieItem
        }
    }

    fun insertMoviesInDatabase(listDB: List<MovieDB>) {
        dbProvider.insert(listDB)
    }

    override fun onRefresh() {
        returnAllPopularMovies()
    }

    /**
     * Function that set this movieItem as Favourite or noFavourite
     */
    fun pressFavButton(movieItem: MovieItem) {

        val attrFav: Boolean

        if (movieItem.favourite) {
            attrFav = false

            // Remove favourite attribute of the movies database
            dbProvider.removeFavourite(movieItem.id)

        } else {
            attrFav = true

            // Include favourite attribute of the movies database
            dbProvider.setFavourite(movieItem.id)

        }

        //find movie that click movie and change the favourite attribute
        popularMovieModel.postValue(
            popularMovieModel.value?.map { if (it.id == movieItem.id) it.copy(favourite = attrFav) else it }
                ?.toMutableList()
        )

    }

    override fun onQueryTextSubmit(text: String?): Boolean {
        if (text != null) {
            serverMoviesProvider.getMoviesSearched(text, object : GetMoviesServer {
                override fun onSuccess(responseServer: ResponseModel) {
                    // Save in Database
                    val searchMoviesModelDB = dataMapperServer.convertListToMovieDB(responseServer)
                    insertMoviesInDatabase(searchMoviesModelDB)

                    // Set the value data
                    val searchMoviesModelItem =
                        dataMapperServer.convertListToDomainMovieItem(responseServer)
                    popularMovieModel.postValue(searchMoviesModelItem)
                }

                override fun onError() {
                    popularMovieModel.postValue(null)
                }

            })
        }
        return true
    }

    override fun onQueryTextChange(text: String?): Boolean {
        return false
    }

    override fun onClose(): Boolean {
        returnAllPopularMovies()
        return false
    }
}