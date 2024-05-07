package com.github.slznvk.data.api

import com.github.slznvk.domain.dto.AuthState
import com.github.slznvk.domain.dto.Event
import com.github.slznvk.domain.dto.Post
import com.github.slznvk.domain.dto.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @DELETE("api/posts/{id}")
    suspend fun delete(@Path("id") id: Int)

    @POST("api/posts/{id}/likes")
    suspend fun like(@Path("id") id: Int): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislike(@Path("id") id: Int): Response<Post>


    //USERS
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>


    //AUTH
    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun authentication(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<AuthState>

    @Multipart
    @POST("api/users/registration")
    suspend fun registration(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part?
    ): Response<AuthState>


    //EVENTS
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>

}