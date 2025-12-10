package com.ahmed.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.assignment.R
import com.ahmed.assignment.data.Movie
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class MovieAdapter(
    private val onBookClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.VH>(DiffCallback()) {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPoster: ImageView = itemView.findViewById(R.id.iv_movie_poster)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_movie_title)
        private val tvGenre: TextView = itemView.findViewById(R.id.tv_movie_genre)
        private val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val btnBook: MaterialButton = itemView.findViewById(R.id.btn_book)

        fun bind(movie: Movie, onBookClick: (Movie) -> Unit) {
            tvTitle.text = movie.title
            tvGenre.text = movie.genre
            tvDuration.text = movie.duration
            tvRating.text = String.format("%.1f", movie.rating)  // e.g., 8.8

            btnBook.text = if (movie.isNowShowing) "Book Now" else "Coming Soon"
            btnBook.isEnabled = movie.isNowShowing
            btnBook.setOnClickListener { onBookClick(movie) }

            // Load real poster with Glide
            Glide.with(itemView.context)
                .load(movie.posterUrl)
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)  // Fallback if load fails
                .into(ivPoster)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_card_modern, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onBookClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(old: Movie, new: Movie) = old.id == new.id
        override fun areContentsTheSame(old: Movie, new: Movie) = old == new
    }
}