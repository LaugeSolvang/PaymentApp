package com.example.paymentapp.network.api

import com.example.paymentapp.model.Account
import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("groups")
    suspend fun getGroups(): List<Group>

    @PUT("groups/{groupId}")
    suspend fun updateGroup(@Path("groupId") groupId: String, @Body participant: Group)

    @GET("users")
    suspend fun getUsers(): List<Account>

    @POST("groups")
    suspend fun createGroup(@Body group: Group): Group

    @PUT("users/{userId}")
    suspend fun updateUser(@Path("userId") userId: String, @Body account: Account)

    @GET("groups/{groupId}")
    suspend fun getGroup(@Path("groupId") groupId: String): Group

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Account

    @POST("users")
    suspend fun createUser(@Body user: Account): Account

    @GET("groups/{groupId}/participants/{participantId}/expenses")
    suspend fun getExpensesForParticipant(
        @Path("groupId") groupId: String,
        @Path("participantId") participantId: String
    ): List<Expense>
}