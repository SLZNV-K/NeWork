package com.github.slznvk.data.api

import com.github.slznvk.domain.dto.Event
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventApiService {

    @GET("api/events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("api/events/{id}/before")
    suspend fun getBeforeEvents(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("api/events/{id}/after")
    suspend fun getAfterEvents(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @POST("api/events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun removeEventById(@Path("id") id: Int)

    @POST("api/events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Int): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun dislikeEvent(@Path("id") id: Int): Response<Event>

}