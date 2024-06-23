package com.github.slznvk.data.api

import com.github.slznvk.domain.dto.AuthState
import com.github.slznvk.domain.dto.Job
import com.github.slznvk.domain.dto.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {

    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>

    @GET("api/{userId}/jobs")
    suspend fun getUserJobs(@Path("userId") userId: Long): Response<List<Job>>

    @POST("api/my/jobs")
    suspend fun saveJob(@Body job: Job): Response<Job>

    @DELETE("api/my/jobs")
    suspend fun deleteJodById(@Path("id") id: Long)


    //AUTH
    @POST("api/users/authentication")
    suspend fun authentication(
        @Query("login") login: String,
        @Query("pass") pass: String,
    ): Response<AuthState>

    @Multipart
    @POST("api/users/registration")
    suspend fun registration(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part?
    ): Response<AuthState>
}