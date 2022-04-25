package com.example.gotitapplication.model;

/**
 * 评论对象
 */
public class Comment {
    public String mContent; // 评论内容
    public com.example.gotitapplication.model.User mCommentator; // 评论者
    public com.example.gotitapplication.model.User mReceiver; // 接收者（即回复谁）
    //--------------------------5.13-------------------------
    public double tno;
    //--------------------------5.13

    public Comment(com.example.gotitapplication.model.User mCommentator, String mContent,
                   com.example.gotitapplication.model.User mReceiver, double tno) {
        this.mCommentator = mCommentator;
        this.mContent = mContent;
        this.mReceiver = mReceiver;
        this.tno=tno;
    }
}
