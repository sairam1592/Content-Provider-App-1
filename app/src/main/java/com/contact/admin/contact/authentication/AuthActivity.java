package com.contact.admin.contact.authentication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.contact.admin.contact.R;

/**
 * Created by Admin on 11/28/2015.
 */
public class AuthActivity extends Activity {


    Button login, register;
    boolean flag;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        initializeResources();
        onButtonClick();
    }

    public void initializeResources() {
        this.login = (Button) findViewById(R.id.button_login);
        this.register = (Button) findViewById(R.id.button_register);
    }

    public void onButtonClick() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i = new Intent(AuthActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCountMethod()) {
                    i = new Intent(AuthActivity.this, RegisterActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(AuthActivity.this, "Already registered, kindly Login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean getCountMethod() {
        Cursor countCursor = getContentResolver().query(ProviderClass.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursor.moveToFirst();
        int count = countCursor.getInt(0);
        if (count > 0) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }
}
