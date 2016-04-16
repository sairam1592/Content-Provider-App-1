package com.contact.admin.contact.authentication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.contact.admin.contact.MainActivity;
import com.contact.admin.contact.R;

/**
 * Created by Admin on 11/28/2015.
 */
public class LoginActivity extends Activity {

    EditText name, password;
    Button bt_login;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeResources();
        onLoginButtonClick();
    }

    public void initializeResources() {
        this.name = (EditText) findViewById(R.id.editText_name);
        this.password = (EditText) findViewById(R.id.editText_password);
        this.bt_login = (Button) findViewById(R.id.button_login);
    }

    public void onLoginButtonClick() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authenticationSuccess()) {
                    updateFlag("true");
                    i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public boolean authenticationSuccess() {
        boolean flag = false;
        String[] projection = {"name", "password"};
        Cursor cur = getContentResolver().query(ProviderClass.CONTENT_URI, projection, null, null, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            int name_index = cur.getColumnIndex(ProviderClass.name);
            int pwd_index = cur.getColumnIndex(ProviderClass.password);
            if (name.getText().toString().equalsIgnoreCase(cur.getString(name_index)) &&
                    password.getText().toString().equalsIgnoreCase(cur.getString(pwd_index))) {
                flag = true;
            } else {
                flag = false;
            }
        }
        return flag;
    }

    public void updateFlag(String flag) {
        ContentValues updateFlag = new ContentValues();
        updateFlag.put(ProviderClass.authFlag, flag);
        getContentResolver().update(ProviderClass.CONTENT_URI, updateFlag, "name=?", new String[]{name.getText().toString()});
    }
}
