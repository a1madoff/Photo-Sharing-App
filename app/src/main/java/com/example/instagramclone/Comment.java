package com.example.instagramclone;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_POST = "post";
    public static final String KEY_COMMENTER = "commenter";

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public ParseUser getCommenter() {
        return getParseUser(KEY_COMMENTER);
    }

    public void setCommenter(ParseUser commenter) {
        put(KEY_COMMENTER, commenter);
    }
}
