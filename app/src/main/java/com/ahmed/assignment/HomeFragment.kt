// HomeFragment.kt
package com.ahmed.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.assignment.adapter.MovieAdapter
import com.ahmed.assignment.api.ApiService
import com.ahmed.assignment.api.MovieMapper
import com.ahmed.assignment.api.RetrofitClient
import com.ahmed.assignment.data.Movie
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    // UI references
    private lateinit var rvMovies: RecyclerView
    private lateinit var tvUserName: TextView
    private lateinit var progressBar: ProgressBar  // Add this to XML if not there

    private lateinit var movieAdapter: MovieAdapter
    private val apiService: ApiService = RetrofitClient.apiService

    companion object {
        private const val TMDB_API_KEY = "e88a3ceae739f519be136020e84208ba"  // Your key (move to secrets later)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // findViewById
        rvMovies = view.findViewById(R.id.rvMovies)
        tvUserName = view.findViewById(R.id.tvUserName)
        // progressBar = view.findViewById(R.id.progressBar)  // Add <ProgressBar android:id="@+id/progressBar" ... /> to XML

        // Setup RecyclerView
        movieAdapter = MovieAdapter { movie ->
            // TODO: Navigate to detail with movie.id
            Toast.makeText(context, "Book ${movie.title}", Toast.LENGTH_SHORT).show()
        }

        rvMovies.apply {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Load real movies async
        loadRealMovies()

        // Update greeting
        updateUserGreeting()
    }

    private fun loadRealMovies() {
        // Show loading (add ProgressBar to XML: <ProgressBar android:id="@+id/progressBar" style="?android:attr/progressBarStyle" android:layout_width="wrap_content" android:layout_height="wrap_content" android:visibility="gone" app:layout_constraintTop_toTopOf="@id/rvMovies" app:layout_constraintStart_toStartOf="parent" app:layout_constraintEnd_toEndOf="parent"/> )
        // progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch Now Playing + Upcoming (combine for full list)
                val nowPlayingResponse = apiService.getNowPlaying(TMDB_API_KEY)
                val upcomingResponse = apiService.getUpcoming(TMDB_API_KEY)

                val nowPlayingMovies = if (nowPlayingResponse.isSuccessful) {
                    nowPlayingResponse.body()?.results?.map { MovieMapper.toMovie(it, true) } ?: emptyList()
                } else emptyList<Movie>()

                val upcomingMovies = if (upcomingResponse.isSuccessful) {
                    upcomingResponse.body()?.results?.map { MovieMapper.toMovie(it, false) } ?: emptyList()
                } else emptyList<Movie>()

                val allMovies = nowPlayingMovies + upcomingMovies.take(5)  // Limit to 10 total

                withContext(Dispatchers.Main) {
                    movieAdapter.submitList(allMovies)
                    // progressBar.visibility = View.GONE
                    if (allMovies.isEmpty()) {
                        Toast.makeText(context, "Failed to load movies. Check internet.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error loading movies: ${e.message}", Toast.LENGTH_SHORT).show()
                    // progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUserGreeting() {
        val user = FirebaseAuth.getInstance().currentUser
        tvUserName.text = user?.displayName?.let { "Hi, $it!" } ?: "Hi, Guest!"
    }
}