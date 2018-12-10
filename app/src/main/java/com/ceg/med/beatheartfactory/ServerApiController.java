package com.ceg.med.beatheartfactory;

import com.ceg.med.beatheartfactory.client.api.ServerApi;
import com.ceg.med.beatheartfactory.client.model.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerApiController {

    private Logger LOG = Logger.getLogger(ServerApiController.class.getName());

    public static final String API_BASE_URL = "https://beatheartserver.herokuapp.com/";

    private static ServerApiController instance = null;

    private ServerApi serverApi;

    public static ServerApiController getInstance() {
        if (instance == null) {
            instance = new ServerApiController();
        }
        return instance;
    }

    private ServerApiController() {
        initServerApi();
    }

    private void initServerApi() {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        Retrofit retrofit = builder.client(new okhttp3.OkHttpClient()).build();
        serverApi = retrofit.create(ServerApi.class);
    }

    public UserRunnable postUser(User user) {
        return new UserRunnable(user);
    }

    public class UserRunnable implements Runnable, Serializable {

        private User user;

        public UserRunnable(User user) {
            this.user = user;
        }

        @Override
        public void run() {
            try {
                Integer id = serverApi.postUser(user).execute().body();
                this.user.id(id);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Unable to send user info to API", e);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Other exceptions occurred", e);
            }
        }

        private User putUser(User user) {
            try {
                Integer id = serverApi.postUser(user).execute().body();
                return user.id(id);
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Unable to send user info to API", e);
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Other exceptions occurred", e);
            }
            return user;
        }

        public User getUser() {
            return user;
        }

    }

}
