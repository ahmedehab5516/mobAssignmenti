package com.ahmed.cinema.di

import com.ahmed.cinema.data.api.MovieApiService
import com.ahmed.cinema.data.repository.IMovieRepository
import com.ahmed.cinema.data.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(apiService: MovieApiService): IMovieRepository =
        MovieRepository(apiService)
}
