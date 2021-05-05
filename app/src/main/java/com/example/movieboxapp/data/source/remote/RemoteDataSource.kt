package com.example.movieboxapp.data.source.remote

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.movieboxapp.BuildConfig
import com.example.movieboxapp.data.source.remote.response.*
import com.example.movieboxapp.utils.retrofit.ApiClient
import com.example.movieboxapp.utils.retrofit.EspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource {


    val api_key = BuildConfig.ApiKey
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val SERVICE_LATENCY_IN_MILLIS: Long = 2000

        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource = instance ?: synchronized(this) {
            RemoteDataSource().apply { instance = this }
        }
    }

    fun getAllMovies(callback: GetAllMoviesCallback) {
        EspressoIdlingResource.increment()
        ApiClient.getApiInterface().getMovies(api_key).enqueue(object : Callback<ResponseMovie> {
            override fun onResponse(call: Call<ResponseMovie>, response: Response<ResponseMovie>) {
                handler.postDelayed({
                    callback.getAllMoviesAsync(response.body()?.results)
                    EspressoIdlingResource.decrement()
                }, SERVICE_LATENCY_IN_MILLIS)
            }

            override fun onFailure(call: Call<ResponseMovie>, t: Throwable) {
                Log.d(this@RemoteDataSource.toString(), "get error : ${t.message}")
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun getAllTvshow(callback: GetAllTvshowCallback) {
        EspressoIdlingResource.increment()
        ApiClient.getApiInterface().getTvshows(api_key).enqueue(object : Callback<ResponseTvshow> {
            override fun onResponse(
                call: Call<ResponseTvshow>,
                response: Response<ResponseTvshow>
            ) {
                handler.postDelayed({
                    callback.getAllTvshowAsync(response.body()?.results)
                    EspressoIdlingResource.decrement()
                }, SERVICE_LATENCY_IN_MILLIS)
            }

            override fun onFailure(call: Call<ResponseTvshow>, t: Throwable) {
                Log.d(this@RemoteDataSource.toString(), "get error : ${t.message}")
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun getMovieDetail(callback: GetDetailMovieCallback, id: Int) {
        EspressoIdlingResource.increment()
        ApiClient.getApiInterface().getDetailMovie(id, api_key)
            .enqueue(object : Callback<ResponseDetailMovie> {
                override fun onResponse(
                    call: Call<ResponseDetailMovie>,
                    responseMovie: Response<ResponseDetailMovie>
                ) {
                    handler.postDelayed({
                        callback.getDetailMovieCallback(responseMovie.body())
                        EspressoIdlingResource.decrement()
                    }, SERVICE_LATENCY_IN_MILLIS)
                }

                override fun onFailure(call: Call<ResponseDetailMovie>, t: Throwable) {
                    Log.d(this@RemoteDataSource.toString(), "get error : ${t.message}")
                    EspressoIdlingResource.decrement()
                }
            })
    }

    fun getTvshowDetail(callback: GetDetailTvshowCallback, id: Int) {
        EspressoIdlingResource.increment()
        ApiClient.getApiInterface().getDetailTvshow(id, api_key)
            .enqueue(object : Callback<ResponseDetailTvshow> {
                override fun onResponse(
                    call: Call<ResponseDetailTvshow>,
                    response: Response<ResponseDetailTvshow>
                ) {
                    handler.postDelayed({
                        callback.getDetailTvshowCallback(response.body())
                        EspressoIdlingResource.decrement()
                    }, SERVICE_LATENCY_IN_MILLIS)
                }

                override fun onFailure(call: Call<ResponseDetailTvshow>, t: Throwable) {
                    Log.d(this@RemoteDataSource.toString(), "get error : ${t.message}")
                    EspressoIdlingResource.decrement()
                }
            })
    }

    interface GetAllMoviesCallback {
        fun getAllMoviesAsync(movies: List<DataMovie>?)
    }

    interface GetAllTvshowCallback {
        fun getAllTvshowAsync(tvshows: List<DataTvshow>?)
    }

    interface GetDetailMovieCallback {
        fun getDetailMovieCallback(movie: ResponseDetailMovie?)
    }

    interface GetDetailTvshowCallback {
        fun getDetailTvshowCallback(tvshow: ResponseDetailTvshow?)
    }
}