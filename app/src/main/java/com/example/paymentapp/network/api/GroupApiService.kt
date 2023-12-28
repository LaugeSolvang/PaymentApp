package com.example.paymentapp.network.api

import com.example.paymentapp.model.Expense
import com.example.paymentapp.model.Group
import com.example.paymentapp.model.Participant
import com.example.paymentapp.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupApiService {
    @GET("groups")
    suspend fun getGroups(): List<Group>

    @PUT("groups/{groupId}")
    suspend fun updateGroup(@Path("groupId") groupId: String, @Body participant: Group)

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("groups/{groupId}/participants/{participantId}/expenses")
    suspend fun getExpensesForParticipant(
        @Path("groupId") groupId: String,
        @Path("participantId") participantId: String
    ): List<Expense>
}