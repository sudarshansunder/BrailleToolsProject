package com.braille.os;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import me.everything.providers.android.telephony.Sms;

import static com.braille.os.MessageActivity.smsList;

public class MessageConvoActivity extends AppCompatActivity {

    private ArrayList<Sms> conversation = new ArrayList<>();
    private int index = -1;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard_braille);
        Button button0 = (Button) findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setText("Next");
        button4.setText("Previous");
        button3.setText("Reply");
        textToSpeech = new TextToSpeech(MessageConvoActivity.this, new TextToSpeech.OnInitListener() {
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
        String number = getIntent().getStringExtra("phone");
        String name = getIntent().getStringExtra("displayName");
        Log.d("Convo number", number);
        Log.d("Convo name", name);
        for (Sms obj : smsList) {

            String address = obj.address;
            address = address.substring(Math.max(0, address.length() - 10));
            Log.d("Address", address);
            if (number.contains(address) || name.contains(address)) {
                conversation.add(obj);
            }
        }
        Log.d("Conversations", "" + conversation.size());
        Log.d("Conversations", conversation.toString());
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (index < conversation.size() - 1) {
                    index++;
                    ConvertTextToSpeech(conversation.get(index).body);
                    //Toast.makeText(getApplicationContext(), conversation.get(index).body, Toast.LENGTH_SHORT).show();
                    Log.d("Sms Body", conversation.get(index).body + " " + conversation.get(index).address);
                    //textToSpeech.speak(contactList.get(index).displayName, TextToSpeech.QUEUE_FLUSH, null, null);

                } else {
                    ConvertTextToSpeech("You've reached the end");
                    //Toast.makeText(getApplicationContext(), "You've reached the end", Toast.LENGTH_SHORT).show();
                }

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= 0) {
                    ConvertTextToSpeech(conversation.get(index).body);
                    Log.d("Sms Body", conversation.get(index).body);
                    index--;
                    index = Math.max(-1, index);
                } else {
                    ConvertTextToSpeech("You've reached the beginning");
                    //Toast.makeText(getApplicationContext(), "You've reached the beginning", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), KeyboardInputActivity.class), 13);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 13) {
            if (resultCode == RESULT_OK) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(conversation.get(index).address, null, data.getStringExtra("String"), null, null);
                //Toast.makeText(getApplicationContext(), "SMS has successfully been sent to " + contactList.get(index).displayName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ConvertTextToSpeech(String a) {
        if (a == null || "".equals(a)) {
            textToSpeech.speak("No Text Was Typed!", TextToSpeech.QUEUE_FLUSH, null, a);
        } else {
            textToSpeech.speak(a, TextToSpeech.QUEUE_FLUSH, null, a);
        }
    }

}
