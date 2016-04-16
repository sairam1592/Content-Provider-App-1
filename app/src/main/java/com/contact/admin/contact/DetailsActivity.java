package com.contact.admin.contact;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arun on 11/27/2015.
 */
public class DetailsActivity extends Activity {

    TextView name, number, address, company, title, email, nickname, dob;
    ArrayList<HashMap<String, String>> myContacts;
    String contactName, contactNumber;
    RelativeLayout rel3, rel4, rel5;
    ImageView img;
    Bitmap bitmap = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent i = getIntent();
        myContacts = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra(Constants.TAG_LISTKEY);
        contactName = i.getStringExtra(Constants.TAG_NAMEKEY);
        contactNumber = i.getStringExtra(Constants.TAG_NUMBERKEY);
        initializeResources();
        setData();
    }

    public void initializeResources() {
        this.name = (TextView) findViewById(R.id.textView_nameVal);
        this.number = (TextView) findViewById(R.id.textView_numberVal);
        this.address = (TextView) findViewById(R.id.textView_addrVal);
        this.company = (TextView) findViewById(R.id.textView_compVal);
        this.title = (TextView) findViewById(R.id.textView_titleVal);
        this.email = (TextView) findViewById(R.id.textView_emailVal);
        this.nickname = (TextView) findViewById(R.id.textView_nicknameVal);
        this.dob = (TextView) findViewById(R.id.textView_dobVal);
        this.rel3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
        this.rel4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        this.rel5 = (RelativeLayout) findViewById(R.id.relativeLayout5);
        this.img = (ImageView) findViewById(R.id.imageView2);
    }

    public void setData() {
        for (int i = 0; i < myContacts.size(); i++) {
            if (myContacts.get(i).get(Constants.TAG_NAME).toString().contains(contactName) && myContacts.get(i).get(Constants.TAG_NUMBER).toString().contains(contactNumber)) {
                name.setText(myContacts.get(i).get(Constants.TAG_NAME).toString());
                number.setText(myContacts.get(i).get(Constants.TAG_NUMBER).toString());

                String add = formAddress(myContacts.get(i));
                if (!add.equalsIgnoreCase("false")) {
                    address.setText(add);
                } else {
                    rel3.setVisibility(RelativeLayout.GONE);
                }
                formCompTitle(myContacts.get(i));
                formOtherDetails(myContacts.get(i));
                break;
            }
        }
    }

    public String formAddress(HashMap<String, String> contactDetail) {
        StringBuilder sb = new StringBuilder();
        if (contactDetail.containsKey(Constants.TAG_STREET)) {
            sb.append(contactDetail.get(Constants.TAG_STREET) + ",");
        }
        if (contactDetail.containsKey(Constants.TAG_CITY)) {
            sb.append(contactDetail.get(Constants.TAG_CITY) + ",");
        }
        if (contactDetail.containsKey(Constants.TAG_STATE)) {
            sb.append(contactDetail.get(Constants.TAG_STATE) + ",");
        }
        if (contactDetail.containsKey(Constants.TAG_POSTAL)) {
            sb.append(contactDetail.get(Constants.TAG_POSTAL) + ".");
        }
        if ((!contactDetail.containsKey(Constants.TAG_STREET)) && !contactDetail.containsKey(Constants.TAG_CITY)
                && !contactDetail.containsKey(Constants.TAG_STATE) && !contactDetail.containsKey(Constants.TAG_POSTAL)) {
            sb.append("false");
        }
        return sb.toString();

    }


    public void formCompTitle(HashMap<String, String> contactDetail) {

        if (contactDetail.containsKey(Constants.TAG_COMPANY)) {
            company.setText(contactDetail.get(Constants.TAG_COMPANY));
        }
        if (contactDetail.containsKey(Constants.TAG_TITLE)) {
            title.setText(contactDetail.get(Constants.TAG_TITLE));
        }
        if (!contactDetail.containsKey(Constants.TAG_COMPANY) && !contactDetail.containsKey(Constants.TAG_TITLE)) {
            rel4.setVisibility(RelativeLayout.GONE);
        }
    }

    public void formOtherDetails(HashMap<String, String> contactDetail) {
        if (contactDetail.containsKey(Constants.TAG_EMAIL)) {
            email.setText(contactDetail.get(Constants.TAG_EMAIL));
        }
        if (contactDetail.containsKey(Constants.TAG_DOB)) {
            dob.setText(contactDetail.get(Constants.TAG_DOB));
        }
        if (contactDetail.containsKey(Constants.TAG_NICKNAME)) {
            nickname.setText(contactDetail.get(Constants.TAG_NICKNAME));
        }
        if (!contactDetail.containsKey(Constants.TAG_EMAIL) && !contactDetail.containsKey(Constants.TAG_NICKNAME)
                && !contactDetail.containsKey(Constants.TAG_DOB)) {
            rel5.setVisibility(RelativeLayout.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }


}
