package com.braille.os;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import me.everything.providers.android.telephony.Sms;

import static com.braille.os.MessageActivity.smsList;

public class MessageUnreadActivity extends AppCompatActivity {
    private List<Sms> smsUnreadList;
    int index = 0;
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
        textToSpeech = new TextToSpeech(MessageUnreadActivity.this, new TextToSpeech.OnInitListener() {
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
        smsUnreadList = new ArrayList<Sms>();
        for (int i = 0; i < smsList.size(); i++) {
            if (!smsList.get(i).read) {
                smsUnreadList.add(smsList.get(i));
            }
        }
        if (smsUnreadList.isEmpty()) {
            ConvertTextToSpeech("No Unread Messages!");
            //Toast.makeText(this, "No Unread Messages!", Toast.LENGTH_SHORT).show();
        } else {
            ConvertTextToSpeech(String.valueOf(index) +" "+ smsUnreadList.get(index).address);
            //Toast.makeText(this, String.valueOf(index) + smsUnreadList.get(index).address, Toast.LENGTH_SHORT).show();
        }
        button2.setText("Search");
        button3.setText("Read");
        button4.setText("Previous");
        button5.setText("Next");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageUnreadActivity.this, KeyboardInputActivity.class);
                startActivityForResult(intent, 15);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertTextToSpeech(smsUnreadList.get(index).body);
                //Toast toast = new Toast(MessageUnreadActivity.this);
                //toast.makeText(MessageUnreadActivity.this, smsUnreadList.get(index).body, Toast.LENGTH_SHORT).show();
                Sms sms = smsUnreadList.get(index);
                smsList.remove(sms);
                sms.read = true;
                smsUnreadList.remove(index);
                smsList.add(sms);
                Paper.book().write("SmsList", smsList);

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= 0) {
                    ConvertTextToSpeech(String.valueOf(index)+" "+smsUnreadList.get(index).address);
                    //Toast toast = new Toast(MessageUnreadActivity.this);
                    //toast.makeText(MessageUnreadActivity.this, String.valueOf(index) + smsUnreadList.get(index).address, Toast.LENGTH_SHORT).show();
                    index--;
                } else {
                    ConvertTextToSpeech("That was your first unread message!");
                    //Toast toast = new Toast(MessageUnreadActivity.this);
                    //toast.makeText(MessageUnreadActivity.this, "That was your first unread message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < smsUnreadList.size() - 1) {
                    index++;
                    ConvertTextToSpeech(String.valueOf(index) + " " + smsUnreadList.get(index).address);
                    //Toast toast = new Toast(MessageUnreadActivity.this);
                    //toast.makeText(MessageUnreadActivity.this, String.valueOf(index) + smsUnreadList.get(index).address, Toast.LENGTH_SHORT).show();
                } else {
                    ConvertTextToSpeech("That was your last unread message!");
                    //Toast toast = new Toast(MessageUnreadActivity.this);
                    //toast.makeText(MessageUnreadActivity.this, "That was your last unread message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 15) {
            if (resultCode == RESULT_OK) {
                String searchResult = data.getStringExtra("String");
                Log.d("Search Number", searchResult);
                searchThroughList(searchResult);
                ConvertTextToSpeech(smsUnreadList.get(index).address);
                //Toast toast = new Toast(MessageUnreadActivity.this);
                //toast.makeText(MessageUnreadActivity.this, smsUnreadList.get(index).address, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchThroughList(String toFind) {
        List<Sms> searchList = new ArrayList<>();
        for (int i = 0; i < smsUnreadList.size(); i++) {
            if (smsUnreadList.get(i).address.length() < toFind.length())
                continue;
            if (smsUnreadList.get(i).address.substring(0, toFind.length()).equalsIgnoreCase(toFind)) {
                searchList.add(smsUnreadList.get(i));
            }
        }
        if (!searchList.isEmpty()) {
            smsUnreadList.clear();
            smsUnreadList.addAll(searchList);
            Log.d("Search list size", smsUnreadList.size() + "");
            index = -1;
            Log.d("New List", searchList.toString());
        } else {
            ConvertTextToSpeech("Results not found!");
            //Toast toast = new Toast(MessageUnreadActivity.this);
            //toast.makeText(MessageUnreadActivity.this, "Results not found!", Toast.LENGTH_SHORT).show();
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
