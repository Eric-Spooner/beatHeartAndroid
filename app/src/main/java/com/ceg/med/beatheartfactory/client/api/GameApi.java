package com.ceg.med.beatheartfactory.client.api;

import com.ceg.med.beatheartfactory.client.model.Activity;
import com.ceg.med.beatheartfactory.client.model.GameStatistics;

import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GameApi {
    /**
     * Posts new game statistics
     * Sync method
     * Posts a new game statistic
     *
     * @param userId ID of pet to return (required)
     * @param body   (optional)
     * @return Void
     */

    @POST("/user/{userId}/gameStats")
    Void postGameStats(
            @Path("userId") Integer userId, @Body GameStatistics body
    );

    /**
     * Posts new game statistics
     * Async method
     *
     * @param userId ID of pet to return (required)
     * @param body   (optional)
     * @param cb     callback method
     */

    @POST("/user/{userId}/gameStats")
    void postGameStats(
            @Path("userId") Integer userId, @Body GameStatistics body, Callback<Void> cb
    );

    /**
     * Posts new general statistics
     * Sync method
     * Posts a new tatistic
     *
     * @param userId ID of pet to return (required)
     * @param body   (optional)
     * @return Void
     */

    @POST("/user/{userId}/stats")
    Void postStats(
            @Path("userId") Integer userId, @Body Activity body
    );

    /**
     * Posts new general statistics
     * Async method
     *
     * @param userId ID of pet to return (required)
     * @param body   (optional)
     * @param cb     callback method
     */

    @POST("/user/{userId}/stats")
    void postStats(
            @Path("userId") Integer userId, @Body Activity body, Callback<Void> cb
    );
}
