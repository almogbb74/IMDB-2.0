package com.example.moviereview.data.di

import com.example.moviereview.data.api.PlacesApiService
import com.example.moviereview.data.api.TMDbService
import com.example.moviereview.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("moviesRetrofit")
    fun provideTMDbRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTMDbService(@Named("moviesRetrofit") retrofit: Retrofit): TMDbService {
        return retrofit.create(TMDbService::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    @Named("placesRetrofit")
    fun providePlacesRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.PLACES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePlacesApiService(@Named("placesRetrofit") retrofit: Retrofit): PlacesApiService {
        return retrofit.create(PlacesApiService::class.java)
    }
}