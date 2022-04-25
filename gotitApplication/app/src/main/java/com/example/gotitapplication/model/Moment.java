package com.example.gotitapplication.model;

import java.util.ArrayList;

/**
 * 评论对象
 */
public class Moment {

    public int tnoa;//动态号
    public String mContent;//动态内容
    public String content_image;//动态图片地址
    public String content_uname;//用户名
    public String content_upath;//用户头像
    public ArrayList<Comment> mComment; // 评论列表
    public String dtime;//发布时间

    public Moment(String mContent,ArrayList<Comment> mComment,int tnoa, String content_image, String content_uname, String content_upath, String dtime) {
        this.mComment = mComment;
        this.mContent = mContent;
        this.tnoa=tnoa;
        this.content_image=content_image;
        this.content_uname=content_uname;
        this.content_upath=content_upath;
        this.dtime = dtime;
    }
}
