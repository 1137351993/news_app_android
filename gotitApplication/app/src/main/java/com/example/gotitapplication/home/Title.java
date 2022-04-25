package com.example.gotitapplication.home;

public class Title {
    private String id;
    private String title;
    private String descr;
    private String imageUrl;


    public Title(String id, String title,String descr, String imageUrl){
        this.id=id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.descr = descr;
    }

    public String getId() { return id; }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescr() {
        return descr;
    }

}