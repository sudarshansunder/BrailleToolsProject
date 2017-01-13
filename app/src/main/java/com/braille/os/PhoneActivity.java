package com.braille.os;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class PhoneActivity extends AppCompatActivity {

    private List<Contact> contactList;
    private int index = -1;
    private boolean inSearchMode = false;
    TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard_braille);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Error", "This Language is not Supported");
                    }
                } else {
                    Log.e("Error", "Intilization Failed!");
                }
            }
        });
        contactList = new ContactsProvider(PhoneActivity.this).getContacts().getList();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                contactList = Utils.removeDuplicates(contactList);
                Utils.sort(contactList);
                textToSpeech.speak(contactList.get(0).displayName, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
        Button button0 = (Button) findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setText("Next");
        button4.setText("Previous");
        button3.setText("Search");
        button2.setText("New number");
        button0.setText("Make call");
        textToSpeech = new TextToSpeech(PhoneActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Error", "This Language is not Supported");
                    } else {
                        //ConvertTextToSpeech("1 Unread Messages, 2 Compose Message, 3 View Conversations");
                    }
                } else {
                    Log.e("Error", "Intilization Failed!");
                }
            }
        });
        Log.d("Contact Size", "" + contactList.size());
        Log.d("Contact List", contactList.toString());
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textToSpeech.speak("Top Right, Make Call,          Middle, Enter a New Number and Make Call,         Bottom, Previous Contact and Next Contact", TextToSpeech.QUEUE_FLUSH, null, null);
                return true;
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < contactList.size() - 1) {
                    index++;
                    //Toast.makeText(getApplicationContext(), contactList.get(index).displayName, Toast.LENGTH_SHORT).show();
                    Log.d("Contact", contactList.get(index).displayName + " " + contactList.get(index).phone);
                    textToSpeech.speak(contactList.get(index).displayName, TextToSpeech.QUEUE_FLUSH, null, null);

                } else {
                    ConvertTextToSpeech("You've reached the end");
                    //Toast.makeText(getApplicationContext(), "You've reached the end", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), KeyboardInputNumberActivity.class), 69);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= 0) {
                    textToSpeech.speak(contactList.get(index).displayName, TextToSpeech.QUEUE_FLUSH, null, null);
                    Log.d("Contact", contactList.get(index).displayName);
                    index--;
                    index = Math.max(-1, index);
                    //Toast.makeText(getApplicationContext(), contactList.get(index).displayName, Toast.LENGTH_SHORT).show();
                } else {
                    ConvertTextToSpeech("You've reached the beginning");
                    //Toast.makeText(getApplicationContext(), "You've reached the beginning", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber;
                if (index == -1)
                    phoneNumber = contactList.get(0).phone;
                else
                    phoneNumber = contactList.get(index).phone;
                Log.d("Phone number", phoneNumber);
                makeCall(phoneNumber);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inSearchMode) {
                    startActivityForResult(new Intent(getApplicationContext(), KeyboardInputActivity.class), 13);
                    inSearchMode = true;
                }
            }
        });
    }

    private void makeCall(String number) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", number, null));
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 69) {
            if (resultCode == RESULT_OK) {
                String phoneNumber = data.getStringExtra("String");
                Log.d("Phone number", phoneNumber);
                makeCall(phoneNumber);
            }
        } else if (requestCode == 13) {
            if (resultCode == RESULT_OK) {
                String searchResult = data.getStringExtra("String");
                Log.d("Search Number", searchResult);
                searchThroughList(searchResult);
                inSearchMode = false;
            } else {
                inSearchMode = false;
            }
        }
    }

    private void searchThroughList(String toFind) {
        List<Contact> searchList = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            if (contactList.get(i).displayName.length() < toFind.length())
                continue;
            if (contactList.get(i).displayName.substring(0, toFind.length()).equalsIgnoreCase(toFind)) {
                searchList.add(contactList.get(i));
            }
        }
        if (!searchList.isEmpty()) {
            contactList.clear();
            contactList.addAll(searchList);
            Log.d("Search list size", contactList.size() + "");
            index = -1;
            Log.d("New List", searchList.toString());
        } else {
            textToSpeech.speak("Results not found", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void onResume() {
        super.onResume();
        //textToSpeech.speak(contactList.get(0).displayName, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
    }

    private void ConvertTextToSpeech(String a) {
        if (a == null || "".equals(a)) {
            textToSpeech.speak("No Text Was Typed!", TextToSpeech.QUEUE_FLUSH, null, a);
        } else {
            textToSpeech.speak(a, TextToSpeech.QUEUE_FLUSH, null, a);
        }
    }
}
