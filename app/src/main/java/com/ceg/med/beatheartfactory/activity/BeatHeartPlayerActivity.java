package com.ceg.med.beatheartfactory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ceg.med.beatheartfactory.UnityPlayerActivity;
import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.data.NiniGattCallback;
import com.unity3d.player.UnityPlayer;

import static com.ceg.med.beatheartfactory.activity.MainActivity.BEATH_HEART_FACTORY_LOG_TAG;

public class BeatHeartPlayerActivity extends UnityPlayerActivity implements CallbackAble<Integer> {

    private boolean active;

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
            Log.d(BEATH_HEART_FACTORY_LOG_TAG, ex.toString());
        }
    }



    @Override
    public void callback(Integer value) {
        if (active && value < 5) {
            active = false;
            sendValue(0);
        } else if (value > 5) {
            active = true;
            sendValue(((int)((float)value/DetailActivity.maxVal * 100)));
        }
    }

    private void sendValue(int value) {
        UnityPlayer.UnitySendMessage("GameController", "androidValue", String.valueOf(value));
    }

}
