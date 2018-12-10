package com.ceg.med.beatheartfactory.client.api;

import com.ceg.med.beatheartfactory.client.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServerApi {
    /**
     * Get the server by id
     * Sync method
     *
     * @param userId ID of pet to return (required)
     * @return User
     */

    @GET("/user/{userId}")
    Call<User> getUser(
            @Path("userId") Integer userId
    );

    /**
     * Get the server by id
     * Async method
     *
     * @param userId ID of pet to return (required)
     * @param cb     callback method
     */

    @GET("/user/{userId}")
    void getUser(
            @Path("userId") Integer userId, Callback<User> cb
    );

    /**
     * Posts a new server
     * Sync method
     * Posts a new server
     *
     * @param body (optional)
     * @return Integer
     */

    @POST("/user")
    Call<Integer> postUser(
            @Body User body
    );

    /**
     * Posts a new server
     * Async method
     *
     * @param body (optional)
     * @param cb   callback method
     */

    @POST("/user")
    void postUser(
            @Body User body, Callback<Integer> cb
    );
}
