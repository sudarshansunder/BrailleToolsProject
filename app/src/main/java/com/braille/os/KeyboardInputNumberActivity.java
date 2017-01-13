package com.braille.os;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class KeyboardInputNumberActivity extends AppCompatActivity {
    public int[] word = new int[6];
    public String string = "";
    BrailleMap map = new BrailleMap();
    BrailleNumberMap numberMap = new BrailleNumberMap();
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keyboard_braille);
        clearWord(word);
        string = "";
        map.set();
        numberMap.set();
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Button button0 = new Button(this);
        button0 = (Button) findViewById(R.id.button0);
        Button button1 = new Button(this);
        button1 = (Button) findViewById(R.id.button1);
        Button button2 = new Button(this);
        button2 = (Button) findViewById(R.id.button2);
        Button button3 = new Button(this);
        button3 = (Button) findViewById(R.id.button3);
        Button button4 = new Button(this);
        button4 = (Button) findViewById(R.id.button4);
        Button button5 = new Button(this);
        button5 = (Button) findViewById(R.id.button5);
        textToSpeech = new TextToSpeech(KeyboardInputNumberActivity.this, new TextToSpeech.OnInitListener() {
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
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[0] = 1;
                vibrator.vibrate(150);
                //     Toast.makeText(MainActivity.this,"Button 0",Toast.LENGTH_SHORT).show();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[3] = 1;
                vibrator.vibrate(150);
                //   Toast.makeText(MainActivity.this,"Button 1",Toast.LENGTH_SHORT).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[1] = 1;
                vibrator.vibrate(150);
                // Toast.makeText(MainActivity.this,"Button 2",Toast.LENGTH_SHORT).show();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[4] = 1;
                vibrator.vibrate(150);
                //Toast.makeText(MainActivity.this,"Button 3",Toast.LENGTH_SHORT).show();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[2] = 1;
                vibrator.vibrate(150);
                //Toast.makeText(MainActivity.this,"Button 4",Toast.LENGTH_SHORT).show();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word[5] = 1;
                vibrator.vibrate(150);
                //    Toast.makeText(MainActivity.this,"Button 5",Toast.LENGTH_SHORT).show();
            }
        });
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ConvertTextToSpeech("Numeric Keyboard");
                return true;
            }
        });
        button3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(MainActivity.this,string,Toast.LENGTH_LONG).show();
                ConvertTextToSpeech(string);
                Intent intent = new Intent();
                intent.putExtra("String", string);
                setResult(RESULT_OK, intent);
                finish();
                string = "";
                long[] a = {0, 150, 0, 150};
                vibrator.vibrate(a, -1);
                //return false;
                return true;
            }
        });
        button4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearWord(word);
                string += " ";
                ConvertTextToSpeech("SpaceBar");
                //Toast.makeText(KeyboardInputNumberActivity.this, "Space", Toast.LENGTH_SHORT).show();
                vibrator.vibrate(300);
                //return false;
                return true;
            }
        });
        button2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int len = string.length();
                if (len > 0) {
                    string = string.substring(0, len - 1);
                    long[] a = {0, 150, 0, 150, 0, 150};
                    vibrator.vibrate(a, -1);
                }
                //return false;
                return true;
            }
        });
        button5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int c = 0;
                for (int i = 0; i < 6; i++) {
                    if (word[i] == 1) c += 1 << i;
                }

                Log.v("Mask is ", String.valueOf(c));
                Character ch = numberMap.getBR(c);
                String s = String.valueOf(ch);
                if (ch == '\0') {
                    ConvertTextToSpeech("No such Number");
                    //Toast.makeText(KeyboardInputNumberActivity.this, s + "No such number", Toast.LENGTH_SHORT).show();
                    clearWord(word);
                } else {
                    ConvertTextToSpeech(s);
                    //Toast.makeText(KeyboardInputNumberActivity.this, s + " " + Arrays.toString(word), Toast.LENGTH_SHORT).show();
                    string = string + s;
                    clearWord(word);
                    long[] a = {0, 150, 0, 150};
                    vibrator.vibrate(a, -1);
                }
                //return false;
                return true;
            }
        });
    }

    public void clearWord(int[] word) {
        for (int i = 0; i < 6; i++) {
            word[i] = 0;
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
