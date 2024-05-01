package com.github.slznvk.data

import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.dto.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    //POSTS
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(id: Int): Response<Post>

//    @GET("api/posts/latest")
//    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>
//
//    @GET("api/posts/{id}/before")
//    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>
//
//    @GET("api/posts/{id}/after")
//    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>


    //USERS
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>


    //EVENTS
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>

}