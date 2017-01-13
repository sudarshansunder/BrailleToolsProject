package com.braille.os;

/**
 * Created by Sudarshan Sunder on 7/17/2016.
 */

public class TweetModel {
    private String userName, tweetBody;

    public TweetModel(String userName, String tweetBody) {
        this.userName = userName;
        this.tweetBody = tweetBody;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTweetBody() {
        return tweetBody;
    }

    public void setTweetBody(String tweetBody) {
        this.tweetBody = tweetBody;
    }
}
