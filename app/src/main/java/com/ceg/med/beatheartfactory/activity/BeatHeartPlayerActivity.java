package com.ceg.med.beatheartfactory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ceg.med.beatheartfactory.UnityPlayerActivity;
import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.data.NiniGattCallback;
import com.unity3d.player.UnityPlayer;

import static com.ceg.med.beatheartfactory.activity.MainActivity.HEALTH_CONNECT_LOG_TAG;

public class BeatHeartPlayerActivity extends UnityPlayerActivity implements CallbackAble<Integer> {

    private boolean active;

    private Games game;

    public static BeatHeartPlayerActivity currentActivity;

    public static BeatHeartPlayerActivity instance() {
        return currentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = false;
        currentActivity = this;
        Intent intent = getIntent();
        try {
            NiniGattCallback.set(this);
        } catch (Exception ex) {
            Log.d(HEALTH_CONNECT_LOG_TAG, ex.toString());
        }
    }

    @Override
    public void callback(Integer value) {
        if(active && value < 5){
            active = false;
        }else if(value > 5){
            active = true;
            flapBird(value);
        }
    }

    private void flapBird(int value) {
        UnityPlayer.UnitySendMessage("GameController", "androidFlap", String.valueOf(value));
    }

}
