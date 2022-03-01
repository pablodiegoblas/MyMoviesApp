package com.example.wembleymoviesapp.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wembleymoviesapp.R
import com.example.wembleymoviesapp.data.API.API
import com.example.wembleymoviesapp.databinding.ItemMovieBinding
import com.example.wembleymoviesapp.domain.MovieItem
import com.example.wembleymoviesapp.ui.view.fragments.FavMoviesFragment
import com.squareup.picasso.Picasso

class FavMoviesAdapter(
    private val favMovies: List<MovieItem>,
    private val onMoreClick: (MovieItem) -> Unit,
    private val onFavouriteClick: (favourite: Pair<MovieItem, ImageView>) -> Unit,
    private val onSharedClick: (MovieItem) -> Unit
) :
    RecyclerView.Adapter<FavMoviesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMovie(favMovies[position])
    }

    override fun getItemCount(): Int = favMovies.size


    inner class ViewHolder(
        private val binding: ItemMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        //private var selected: Boolean = false

        //Charge the elements directly with view binding
        fun bindMovie(movieItem: MovieItem) {
            with(movieItem) {
                poster?.let { loadImage(it, binding.imageViewMovie) }
                binding.textViewTitleMovie.text = title
                //If the movie
                if(favourite) binding.imageViewFavourite.setImageResource(R.drawable.ic_favourite_background_red)
            }

            binding.buttonMore.setOnClickListener{
                onMoreClick(movieItem)
            }
            binding.imageViewFavourite.setOnClickListener{
                onFavouriteClick(movieItem to it as ImageView)
            }
            binding.imageViewShared.setOnClickListener{
                onSharedClick(movieItem)
            }
        }

        private fun loadImage(url: String, imageView: ImageView) =
            Picasso.get().load("${API.IMG_URL}$url").fit().into(imageView)

    }
}