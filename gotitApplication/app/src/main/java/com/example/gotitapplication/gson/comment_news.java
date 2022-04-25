package com.example.gotitapplication.gson;

public class comment_news {
    private String user_name;
    private String content;
    public comment_news(String user_name, String content){
        this.user_name=user_name;
        this.content=content;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
