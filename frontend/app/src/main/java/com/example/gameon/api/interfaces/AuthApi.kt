package com.example.gameon.api.interfaces

import com.example.gameon.classes.Preferences
import com.example.gameon.classes.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @GET("/auth/login")
    suspend fun login(): Response<User>

    @GET("/auth/callback_discord")
    suspend fun discordCallback(@Query("code") code: String): Response<User>

    @POST("/auth/register")
    suspend fun register(@Body preferences: Preferences): Response<User>

    @POST("/auth/logout")
    suspend fun logout(): Response<Any> //returns no content

}