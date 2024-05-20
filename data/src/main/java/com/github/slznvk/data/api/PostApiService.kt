package com.github.slznvk.data.api

import com.github.slznvk.domain.dto.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {

    //POST
    @GET("api/posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBeforePosts(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfterPosts(@Path("id") id: Int, @Query("count") count: Int): Response<List<Post>>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removePostById(@Path("id") id: Int)

    @POST("api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Int): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikePost(@Path("id") id: Int): Response<Post>


//    //WALL
//    @GET("api/{authorId}/wall")
//    suspend fun getUserWall(@Path("authorId") authorId: Int): Response<List<Post>>
//
//    @GET("api/{authorId}/wall/{id}/before")
//    suspend fun getUserWallBefore(
//        @Path("authorId") authorId: Int,
//        @Path("id") id: Int,
//        @Query("count") count: Int
//    ): Response<List<Post>>
//
//    @GET("api/{authorId}/wall/{id}/after")
//    suspend fun getUserWallAfter(
//        @Path("authorId") authorId: Int,
//        @Path("id") id: Int,
//        @Query("count") count: Int
//    ): Response<List<Post>>
//
//    @GET("api/{authorId}/wall/latest")
//    suspend fun getUserWallLatest(
//        @Path("authorId") authorId: Int,
//        @Query("count") count: Int
//    ): Response<List<Post>>

}