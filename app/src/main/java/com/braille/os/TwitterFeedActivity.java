package com.braille.os;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterFeedActivity extends AppCompatActivity {

    private ArrayList<TweetModel> tweets = new ArrayList<>();
    int index = 0;
    TextToSpeech textToSpeech;
    RequestQueue queue;

    private String removeUrl(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i), "").trim();
            i++;
        }
        return commentstr;
    }

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
        queue = Volley.newRequestQueue(this);
        textToSpeech = new TextToSpeech(TwitterFeedActivity.this, new TextToSpeech.OnInitListener() {
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
        queue.add(new StringRequest(Request.Method.GET, "https://api.twitter.com/1.1/statuses/home_timeline.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d("Response", response.toString());
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        tweets.add(new TweetModel(obj.getJSONObject("user").getString("name"), removeUrl(obj.getString("text"))));
                        Log.d("Tweets", tweets.get(i).getUserName() + " SAYS " + tweets.get(i).getTweetBody());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.data != null) {
                    Log.d("Error response", new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("authorization", "OAuth oauth_consumer_key=\"t6WVLOVxaHhI1DigEg4xVqFgm\",oauth_token=\"398449697-XsDCNmGQ1lgCSs8PDSrxfOlpM7yMp04QA1IROKHa\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1468703374\",oauth_nonce=\"at3im3\",oauth_version=\"1.0\",oauth_signature=\"PejyWhHw00h4uAGv1ALEv2fUuC8%3D\"");
                return headers;
            }
        });
        button3.setText("READ");
        button4.setText("PREV");
        button5.setText("NEXT");
        button2.setText("POST");
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ConvertTextToSpeech("Middle, Post Tweet and Read Tweet, Bottom, Previous Tweet and Next Tweet");
                return true;
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), KeyboardInputActivity.class), 50);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweet = tweets.get(index).getTweetBody();
                ConvertTextToSpeech(tweet);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index >= 0) {
                    ConvertTextToSpeech(tweets.get(index).getUserName());
                    //Toast.makeText(TwitterFeedActivity.this, String.valueOf(index) + tweets.get(index).getUserName(), Toast.LENGTH_SHORT).show();
                    index--;
                } else {
                    ConvertTextToSpeech("That was your first Tweet!");
                    Toast.makeText(TwitterFeedActivity.this, "That was your first Tweet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < tweets.size() - 1) {
                    index++;
                    ConvertTextToSpeech(tweets.get(index).getUserName());
                    //Toast toast = new Toast(TwitterFeedActivity.this);
                    //toast.makeText(TwitterFeedActivity.this, String.valueOf(index) + tweets.get(index).getUserName(), Toast.LENGTH_SHORT).show();
                } else {
                    ConvertTextToSpeech("That is your Last Tweet!");
                    //Toast toast = new Toast(TwitterFeedActivity.this);
                    //toast.makeText(TwitterFeedActivity.this, "That's the last tweets!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 50) {
            if (resultCode == RESULT_OK) {
                String tweetBody = data.getStringExtra("String");
                //sendTweet(tweetBody);
                Twitter.getApiClient().getStatusesService().update(tweetBody, null, null, null, null, null, null, null, null, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        Log.d("Tweet", "Posted");
                        ConvertTextToSpeech("Your Tweet was posted!");
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });
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
