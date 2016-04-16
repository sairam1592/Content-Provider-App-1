package com.contact.admin.contact;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.contact.admin.contact.authentication.ProviderClass;

public class MainActivity extends Activity {
    private ListView mListView;
    private ProgressDialog pDialog;
    private Handler updateHandler;
    ArrayList<String> contactList;
    ArrayList<HashMap<String, String>> myContacts;
    Cursor cursor;
    int counter;
    SimpleAdapter adapter;
    Intent i;
    Uri uri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Reading contacts...");
        pDialog.setCancelable(false);
        pDialog.show();
        mListView = (ListView) findViewById(R.id.list);
        myContacts = new ArrayList<HashMap<String, String>>();
        updateHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();
            }
        }).start();
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO Do whatever you want with the list data
                i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra(Constants.TAG_LISTKEY, myContacts);
                i.putExtra(Constants.TAG_NAMEKEY, myContacts.get(position).get(Constants.TAG_NAME));
                i.putExtra(Constants.TAG_NUMBERKEY, myContacts.get(position).get(Constants.TAG_NUMBER));
                startActivity(i);
            }
        });
    }

    public void getContacts() {
        contactList = new ArrayList<String>();
        String phoneNumber = null;
        String email = null;
        String nicknameName = null;
        String wSite = null;
        String street = null;
        String city = null;
        String state = null;
        String postalCode = null;
        String birthday = "";
        String image_uri = "";
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                HashMap<String, String> contMap = new HashMap<String, String>();
                updateHandler.post(new Runnable() {
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    contMap.put(Constants.TAG_NAME, name);

                    //read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, DISPLAY_NAME + " ASC");
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        contMap.put(Constants.TAG_NUMBER, phoneNumber);
                    }
                    phoneCursor.close();

                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        contMap.put(Constants.TAG_EMAIL, email);
                    }
                    emailCursor.close();

                    Cursor address_cursror = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                            new String[]{contact_id}, null);
                    while (address_cursror.moveToNext()) {
                        street = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        contMap.put(Constants.TAG_STREET, street);
                        city = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        contMap.put(Constants.TAG_CITY, city);
                        state = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        contMap.put(Constants.TAG_STATE, state);
                        postalCode = address_cursror.getString(address_cursror.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        contMap.put(Constants.TAG_POSTAL, postalCode);
                    }
                    address_cursror.close();

                    //Read nickname
                    String where_ = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] params = new String[]{String.valueOf(contact_id), ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};
                    Cursor nickname = getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, where_, params, null);
                    while (nickname.moveToNext()) {
                        nicknameName = nickname.getString(nickname.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));
                        String nicknameType = nickname.getString(nickname.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.TYPE));
                        contMap.put(Constants.TAG_NICKNAME, nicknameName);
                    }
                    nickname.close();


                    //Read birthday
                    String columns[] = {
                            ContactsContract.CommonDataKinds.Event.START_DATE,
                            ContactsContract.CommonDataKinds.Event.TYPE,
                            ContactsContract.CommonDataKinds.Event.MIMETYPE,
                    };

                    String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                            " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and "
                            + ContactsContract.Data.CONTACT_ID + " = " + contact_id;

                    String[] selectionArgs = null;
                    String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;

                    Cursor birthdayCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, sortOrder);
                    if (birthdayCur.getCount() > 0) {
                        while (birthdayCur.moveToNext()) {
                            birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                            contMap.put(Constants.TAG_DOB, birthday);
                        }
                    }
                    birthdayCur.close();

                    //Read Organization
                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{contact_id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        contMap.put(Constants.TAG_COMPANY, orgName);
                        contMap.put(Constants.TAG_TITLE, title);
                    }
                    orgCur.close();

                /*    //get Photo
                    uri = getPhotoUri(contact_id);
                    if (uri != null) {
                        contMap.put(Constants.TAG_IMAGE, uri.toString());
                    } else {
                        //do nothing
                    } */
                }


                if (!contMap.isEmpty()) {
                    myContacts.add(contMap);
                }
            }
            Log.i("CONTACTS", "HASHMAP" + myContacts.toString());


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] from = {Constants.TAG_NAME, Constants.TAG_NUMBER, Constants.TAG_EMAIL};
                    int[] to = {R.id.text1, R.id.text2, R.id.text3};
                    adapter = new SimpleAdapter(getApplicationContext(), myContacts, R.layout.list_item, from, to);
                    mListView.setAdapter(adapter);
                    mListView.setTextFilterEnabled(true);
                }
            });
            updateHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);
        }
    }

    public Uri getPhotoUri(String id) {
        try {
            Cursor cur = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
                .parseLong(id));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    @Override
    public void onBackPressed() {
        finish();
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            onSignOutButtonClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSignOutButtonClick() {
        updateFlag("false");
        finish();
    }

    public void updateFlag(String flag) {
        ContentValues updateFlag = new ContentValues();
        updateFlag.put(ProviderClass.authFlag, flag);
        getContentResolver().update(ProviderClass.CONTENT_URI, updateFlag, null, null);
        Log.i("LOGIN", "FLAG UPDATED to" + flag);
    }
}

