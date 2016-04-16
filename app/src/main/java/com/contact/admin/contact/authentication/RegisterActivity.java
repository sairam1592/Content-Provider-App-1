package com.contact.admin.contact.authentication;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.contact.admin.contact.R;

/**
 * Created by Admin on 11/28/2015.
 */
public class RegisterActivity extends Activity {


    EditText name, password, re_enter;
    Button bt_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeResources();
        onButtonClick();
    }

    public void initializeResources() {
        this.name = (EditText) findViewById(R.id.editText_name);
        this.password = (EditText) findViewById(R.id.editText_password);
        this.re_enter = (EditText) findViewById(R.id.editText_reenter);
        this.bt_register = (Button) findViewById(R.id.button_register);
    }

    public void onButtonClick() {
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidUserName() && isValidPassword() && isValidRePassword() && isValidPasswordlength() && isPasswordMatch()) {
                    saveDetails();
                }
            }
        });
    }

    public boolean isValidUserName() {

        if (!name.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            name.setError("Enter username");
            return false;
        }
    }

    public boolean isValidPassword() {
        if (!password.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            password.setError("Enter password");
            return false;
        }
    }

    public boolean isValidPasswordlength() {
        if ((password.getText().length()) >= 4) {
            return true;
        } else {
            password.setError("Password should be atleast 4 characters in length");
            return false;
        }
    }

    public boolean isValidRePassword() {
        if (!re_enter.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else {
            re_enter.setError("Enter password again");
            return false;
        }
    }

    public boolean isPasswordMatch() {
        if (password.getText().toString().equalsIgnoreCase(re_enter.getText().toString())) {
            return true;
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void saveDetails() {
        ContentValues values = new ContentValues();
        values.put(ProviderClass.name, name.getText().toString());
        values.put(ProviderClass.password, password.getText().toString());
        values.put(ProviderClass.authFlag, "false");
        Uri uri = getContentResolver().insert(ProviderClass.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), "Registration successful", Toast.LENGTH_LONG)
                .show();
        finish();
    }
}
