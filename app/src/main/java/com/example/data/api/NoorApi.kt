package com.example.data.api

import com.example.model.Game
import com.example.model.EventPackage
import com.example.model.GalleryImage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface NoorApi {

    @GET("api/games.json")
    suspend fun getGames(): List<Game>

    @GET("api/packages.json")
    suspend fun getPackages(): List<EventPackage>

    @GET("api/gallery.json")
    suspend fun getGallery(): List<GalleryImage>

    @POST("api/bookings")
    suspend fun submitBooking(@Body request: com.example.model.BookingRequest): retrofit2.Response<Unit>

    companion object {
        private const val BASE_URL = "http://nooralmoqdadya.xyz/"

        fun create(): NoorApi {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(NoorApi::class.java)
        }
    }
}
