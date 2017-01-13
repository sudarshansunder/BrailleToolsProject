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
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;
import me.everything.providers.android.telephony.Sms;

public class MessageActivity extends AppCompatActivity {
    public static List<Sms> smsList;
    public static List<Sms> smstempList;
    public List<Sms> smsUnreadList;
    TextToSpeech textToSpeech;
    private List<Contact> contactList;
    private boolean inSearchMode = false;
    private int index = -1;
    private List<Contact> contactListFinal;

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
        contactListFinal = new ContactsProvider(this).getContacts().getList();

        contactListFinal = Utils.removeDuplicates(contactListFinal);
        Utils.sort(contactListFinal);
        //textToSpeech.speak(contactList.get(0).displayName, TextToSpeech.QUEUE_FLUSH, null, null);
        contactList = new ArrayList<>(contactListFinal);
        smsList = Paper.book().read("SmsList");

        smsUnreadList = new ArrayList<>();
        Log.v("Size ", String.valueOf(smsList.size()));
        //Toast.makeText(this, String.valueOf(smsList.size()), Toast.LENGTH_SHORT).show();
        Log.v("Sms size", String.valueOf(smsList.size()));
        textToSpeech = new TextToSpeech(MessageActivity.this, new TextToSpeech.OnInitListener() {
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
        button0.setText("Unread");
        button1.setText("Compose");
        button2.setText("Convo");
        button3.setText("Search");
        button4.setText("Previous");
        button5.setText("Next");
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, MessageUnreadActivity.class);
                startActivity(intent);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, KeyboardInputActivity.class);
                startActivityForResult(intent, 20);
            }
        });
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ConvertTextToSpeech("Top, Unread Messages and Compose Message, Middle, View Conversations and Search, Bottom, Previous Contact and Next Contact");
                return true;
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, MessageConvoActivity.class);
                if (index == -1) {
                    intent.putExtra("phone", contactList.get(0).phone);
                    intent.putExtra("displayName", contactList.get(0).displayName);
                } else {
                    intent.putExtra("phone", contactList.get(index).phone);
                    intent.putExtra("displayName", contactList.get(index).displayName);
                }
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inSearchMode) {
                    contactList.clear();
                    contactList.addAll(contactListFinal);
                    startActivityForResult(new Intent(getApplicationContext(), KeyboardInputActivity.class), 13);
                    inSearchMode = true;
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < contactList.size() - 1) {
                    index++;
                    ConvertTextToSpeech(contactList.get(index).displayName);
                    Log.d("Contact", contactList.get(index).displayName + " " + contactList.get(index).phone);

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
                    Log.d("Contact", contactList.get(index).displayName);
                    ConvertTextToSpeech(contactList.get(index).displayName);
                    index--;
                    index = Math.max(-1, index);
                } else {
                    ConvertTextToSpeech("You've reached the beginning");
                    //Toast.makeText(getApplicationContext(), "You've reached the beginning", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 13) {
            if (resultCode == RESULT_OK) {
                String searchResult = data.getStringExtra("String");
                Log.d("Search Number", searchResult);
                searchThroughList(searchResult);
                inSearchMode = false;
            } else {
                inSearchMode = false;
            }
        } else if (requestCode == 20) {
            if (resultCode == RESULT_OK) {
                SmsManager smsManager = SmsManager.getDefault();
                String name;
                if (index == -1) {
                    name = contactList.get(0).phone;
                } else {
                    name = contactList.get(index).phone;
                }
                smsManager.sendTextMessage(contactList.get(index).phone, null, data.getStringExtra("String"), null, null);
                Sms sms = new Sms();
                sms.address = name;
                sms.read = true;
                sms.body = data.getStringExtra("String");
                smsList.add(sms);
                Paper.book().write("SmsList", smsList);
                ConvertTextToSpeech("SMS has successfully been sent to " + name);
                //Toast.makeText(getApplicationContext(), "SMS has successfully been sent to " + name, Toast.LENGTH_SHORT).show();
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
            Log.d("New List", searchList.toString());
        } else {
            textToSpeech.speak("Results not found", TextToSpeech.QUEUE_FLUSH, null, null);
            contactList.clear();
            contactList.addAll(contactListFinal);
        }
        index = -1;
    }

    private void ConvertTextToSpeech(String a) {
        if (a == null || "".equals(a)) {
            textToSpeech.speak("No Text Was Typed!", TextToSpeech.QUEUE_FLUSH, null, a);
        } else {
            textToSpeech.speak(a, TextToSpeech.QUEUE_FLUSH, null, a);
        }
    }
}
