package com.braille.os;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;

import static com.braille.os.MessageActivity.smsList;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "t6WVLOVxaHhI1DigEg4xVqFgm";
    private static final String TWITTER_SECRET = "s5m7FAbY708w8NE1gSEkTQtsKwGXtzX0gkkfp48Jtcdo4hINiZ";
    private static final String ACCESS_TOKEN = "398449697-XsDCNmGQ1lgCSs8PDSrxfOlpM7yMp04QA1IROKHa";
    private static final String ACCESS_TOKEN_SECRET = "x31tK5o4MAdgITMsyx9ycuf7MqFU0NlHZ2yi9UjixOjzh";
    TextToSpeech textToSpeech;

    BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            String strMessage = "";
            String smsSender = "", smsBody = "";
            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    smsSender = messages[i].getOriginatingAddress();
                    smsBody = messages[i].getMessageBody();
                    Log.d("SmsReceiver", smsSender + " " + smsBody);
                    //Toast.makeText(context, smsSender + " " + smsBody, Toast.LENGTH_SHORT).show();
                    Sms sms = new Sms();
                    sms.address = smsSender;
                    sms.read = false;
                    sms.body = smsBody;
                    smsList = Paper.book().read("SmsList");
                    smsList.add(sms);
                    Paper.book().write("SmsList", smsList);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Twitter twitter = new Twitter(authConfig);
        Fabric.with(this, twitter);
        setContentView(R.layout.keyboard_braille);
        Paper.init(this);
        if (getSharedPreferences("data", MODE_PRIVATE).getBoolean("first_time", true)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE}, 12);
        }
        Button button0 = (Button) findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        button1.setText("Message");
        button0.setText("Phone");
        button2.setText("Keyboard");
        button3.setText("Twitter");
        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
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
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ConvertTextToSpeech("Top, Phone and Message,     Middle Keyboard and Twitter");
                return true;
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KeyboardInputActivity.class);
                startActivityForResult(intent, 11);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PhoneActivity.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TwitterActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 12) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyProvider telephonyProvider = new TelephonyProvider(this);
                    List<Sms> smsList = telephonyProvider.getSms(TelephonyProvider.Filter.ALL).getList();
                    Paper.book().write("SmsList", smsList);
                    getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("first_time", false).apply();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    ConvertTextToSpeech("Can't use the app!");
                    //Toast.makeText(this, "Can't use the app :))))", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String text = data.getStringExtra("String");
                ConvertTextToSpeech(text);
                //Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResume() {
        super.onResume();
        IntentFilter inf = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        inf.setPriority(999);
        registerReceiver(br, inf);
    }

    private void ConvertTextToSpeech(String a) {
        if (a == null || "".equals(a)) {
            textToSpeech.speak("No Text Was Typed!", TextToSpeech.QUEUE_FLUSH, null, a);
        } else {
            textToSpeech.speak(a, TextToSpeech.QUEUE_FLUSH, null, a);
        }
    }

    /*public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }*/
}
