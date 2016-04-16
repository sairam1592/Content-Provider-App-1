package com.contact.admin.contact.authentication;

/**
 * Created by Admin on 11/28/2015.
 */

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.contact.admin.contact.Constants;
import com.contact.admin.contact.MainActivity;
import com.contact.admin.contact.R;

public class SplashActivity extends Activity {

    Animation fadeIn;
    ImageView im;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        animText();
        movetoNextActivity();
    }

    public void animText() {
        im = (ImageView) findViewById(R.id.imageView_splash);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        im.startAnimation(fadeIn);
    }

    public void movetoNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getCountMethod()) {
                    if (checkLoginFlag()) {
                        i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        callAuthActivity();
                    }
                } else {
                    callAuthActivity();
                }

            }
        }, Constants.SPLASH_TIMEOUT);
    }

    public void callAuthActivity() {
        i = new Intent(SplashActivity.this, AuthActivity.class);
        startActivity(i);
    }


    public boolean checkLoginFlag() {
        boolean flag = false;
        String[] projection = {"flag"};
        Cursor cur = getContentResolver().query(ProviderClass.CONTENT_URI, projection, null, null, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int flag_index = cur.getColumnIndex(ProviderClass.authFlag);
            if (cur.getString(flag_index).equalsIgnoreCase("true")) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    public boolean getCountMethod() {
        boolean flag = false;
        Cursor countCursor = getContentResolver().query(ProviderClass.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        if (count > 0) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

}
