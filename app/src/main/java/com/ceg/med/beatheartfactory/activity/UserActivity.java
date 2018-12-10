package com.ceg.med.beatheartfactory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceg.med.beatheartfactory.R;
import com.ceg.med.beatheartfactory.ServerApiController;
import com.ceg.med.beatheartfactory.client.model.User;

import java.io.Serializable;

import static com.ceg.med.beatheartfactory.activity.MainActivity.USER_PARAMETER;

/**
 * The main activity.
 */
public class UserActivity extends AppCompatActivity {

    public static final String BEATH_HEART_FACTORY_LOG_TAG = "BeartHeartFactory";

    public static String ID;

    private TextView firstname;
    private TextView lastname;
    private TextView username;

    private ServerApiController serverApiController;

    public static ServerApiController.UserRunnable userRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
    }

    private void init() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = findViewById(R.id.first_image);
        imageView.setImageDrawable(getDrawable(R.mipmap.hctr));

        firstname = findViewById(R.id.firstname_text);
        lastname = findViewById(R.id.lastname_text);
        username = findViewById(R.id.username_text);

        serverApiController = ServerApiController.getInstance();

        final Button button = findViewById(R.id.button_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // create User
                // TODO use databinding
                User user = new User().username(username.getText().toString()).firstname(firstname.getText().toString()).lastname(lastname.getText().toString());
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                // TODO send user to webserver
                userRunnable = serverApiController.postUser(user);
                Thread userThread = new Thread(userRunnable);
                userThread.start();
                //
                startActivity(nextScreen);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starttoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

