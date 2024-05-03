package com.github.slznvk.data.api

import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.dto.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //POSTS
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(id: Int): Response<Post>

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Int, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Int, @Query("count") count: Int): Response<List<Post>>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun delete(@Path("id") id: Int)

    @POST("posts/{id}/likes")
    suspend fun like(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislike(@Path("id") id: Int): Response<Post>



    //USERS
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>


    //EVENTS
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>

}