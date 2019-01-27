package com.ceg.med.beatheartfactory.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ceg.med.beatheartfactory.data.CallbackAble;
import com.ceg.med.beatheartfactory.data.NiniGattCallback;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import static com.ceg.med.beatheartfactory.activity.MainActivity.BEATH_HEART_FACTORY_LOG_TAG;

public class BeatHeartPlayerActivity extends UnityPlayerActivity implements CallbackAble<Integer> {

    private int active;

    public static BeatHeartPlayerActivity currentActivity;

    public static BeatHeartPlayerActivity instance() {
        return currentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = 0;
        currentActivity = this;
        Intent intent = getIntent();
        try {
            NiniGattCallback.set(this);
        } catch (Exception ex) {
            Log.d(BEATH_HEART_FACTORY_LOG_TAG, ex.toString());
        }
    }

    @Unity
    public void startGame(String jsonUnity) {
    }

    @Override
    public void callback(Integer value) {
        int normVal = ((int) ((float) value / DetailActivity.maxVal * 100));
        if (normVal < 30) {
            active = 0;
            sendValue(0);
        } else if (active < 5 && normVal > 30) {
            active++;
            sendValue(normVal);
        }
    }

    private void sendValue(int value) {
        UnityPlayer.UnitySendMessage("GameController", "androidValue", String.valueOf(value));
    }

}
