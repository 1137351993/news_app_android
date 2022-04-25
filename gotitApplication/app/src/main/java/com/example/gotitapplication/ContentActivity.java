package com.example.gotitapplication;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import static com.example.gotitapplication.MainActivity2.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gotitapplication.gson.comment_news;
import com.example.gotitapplication.home.Title;
import com.example.gotitapplication.login.login;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ContentActivity extends AppCompatActivity {

    private static final int ITEM_SOCIETY = 1;
    private static final int ITEM_COUNTY = 2;
//    private static final int ITEM_INTERNATION = 3;
//    private static final int ITEM_FUN = 4;
//    private static final int ITEM_SPORT = 5;
//    private static final int ITEM_NBA = 6;
//    private static final int ITEM_FOOTBALL = 7;
//    private static final int ITEM_TECHNOLOGY = 8;
//    private static final int ITEM_WORK = 9;
//    private static final int  ITEM_APPLE= 10;
//    private static final int  ITEM_WAR= 11;
//    private static final int  ITEM_INTERNET= 12;
//    private static final int  ITEM_TREVAL= 13;
//    private static final int  ITEM_HEALTH= 14;
//    private static final int  ITEM_STRANGE= 15;
//    private static final int  ITEM_LOOKER= 16;
//    private static final int  ITEM_VR= 17;
//    private static final int  ITEM_IT= 18;

    private String id;
    private String source;
    private String title;
    private String datetime;
    private String content;
    private String account;
    private String comment_content;
    private String user_name;

    private int itemName;

    private TextView title_view, source_view, datetime_view;
    private Button button_attention, button_comment;
    private EditText comment;

    private List<comment_news> commentList = new ArrayList<comment_news>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        title_view=(TextView)findViewById(R.id.title);
        source_view=(TextView)findViewById(R.id.source);
        datetime_view=(TextView)findViewById(R.id.datetime);
        button_attention=(Button)findViewById(R.id.attention);
        button_comment=(Button)findViewById(R.id.comment_button);
        comment=(EditText)findViewById(R.id.comment);

        id = getIntent().getStringExtra("id");
        itemName = getIntent().getIntExtra("itemName", 1);
        account = getIntent().getStringExtra("account");

        pull_content();

        title_view.setText(title+"\n");
        source_view.setText(source+"\n");
        datetime_view.setText(datetime);

        LinearLayout linearLayout;
        linearLayout=(LinearLayout)findViewById(R.id.MyTable);
        String[] news = content.split("@");
        for(int i=0;i<news.length;i++){
            if(news[i].indexOf("https")!=-1) {
                ImageView imageView=new ImageView(this);
                linearLayout.addView(imageView);
                Glide.with(this).
                        load(news[i])
                        .override(700, 700)
//                        .error( R.drawable.error) //异常时候显示的图片
//                        .placeholder( R.drawable.error) //加载成功前显示的图片
//                        .fallback( R.drawable.error) //url为空的时候,显示的图片
                        .into(imageView);
                //Log.i(TAG, "onCreate: wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww" + news[i]);
            } else {
                news[i]="\n       "+news[i];
                TextView textView = new TextView(this);
                textView.setText(news[i]);
                linearLayout.addView(textView);
            }
            //Log.i(TAG, "onCreate: lllllllllllllllllllll"+news[i]);
        }

        TextView textView = new TextView(this);
        textView.setText("\n——————————————————————————————\n");
        linearLayout.addView(textView);

        init_comment();

        for(int i=0;i<commentList.size();i++){
            TextView textView1 = new TextView(this);
            textView1.setText(commentList.get(i).getUser_name()+":\n\t"+commentList.get(i).getContent()+"\n\n\n");
            linearLayout.addView(textView1);
        }


        push_history();

        button_comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                comment_content=comment.getText().toString();
                push_comment();
                Toast toast= Toast.makeText(ContentActivity.this, "评论成功"+comment_content, Toast.LENGTH_SHORT);
                toast.show();
                TextView textView = new TextView(ContentActivity.this);
                textView.setText(user_name+":\n\t"+comment_content+"\n\n\n");
                linearLayout.addView(textView);
                comment.setText("");
            }
        });


        button_attention.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                push_attention();
                Toast toast= Toast.makeText(ContentActivity.this, "关注成功"+itemName, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void init_comment(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("id",id);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/comment_news/init") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    commentList.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String user_name=jsonObject.getString("user_name");
                        String content=jsonObject.getString("content");

                        comment_news comment = new comment_news(user_name, content);
                        commentList.add(comment);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread.join();
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }

    private void push_comment(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("account",account);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/users/pull") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONObject jsonObject = new JSONObject(responseData); //将文本格式的JSON转换为JSON数组
                    user_name=jsonObject.getString("user_name");

                    String json = "{\"user_name\":\""+user_name+"\",\"id\":\""+id+"\",\"content\":\""+comment_content+"\"}";
                    OkHttpClient client2 = new OkHttpClient(); //创建http客户端
                    Request request2 = new Request.Builder()
                            .url("http://10.0.2.2:8989/comment_news/push") //后端请求接口的地址
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build(); //创建http请求
                    client2.newCall(request2).execute(); //执行发送指令
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread.join();
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }


    private void pull_content(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String url=response(itemName);
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("id",id);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url(url) //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONObject jsonObject = new JSONObject(responseData); //将文本格式的JSON转换为JSON数组
                    id=jsonObject.getString("id");
                    source=jsonObject.getString("source");
                    title=jsonObject.getString("title");
                    datetime=jsonObject.getString("datetime");
                    content=jsonObject.getString("content");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread.join();
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }

    private void push_history(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String json = "{\"id\":\""+id+"\",\"account\":\""+account+"\",\"type\":"+itemName+"}";
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/history/push") //后端请求接口的地址
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build(); //创建http请求
                    client.newCall(request).execute(); //执行发送指令
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread.join();
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }

    private void push_attention(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String json = "{\"id\":\""+id+"\",\"account\":\""+account+"\",\"type\":"+itemName+"}";
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/attention/push") //后端请求接口的地址
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build(); //创建http请求
                    client.newCall(request).execute(); //执行发送指令
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread.join();
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }

    private String response(int itemName){
        String address = "http://10.0.2.2:8989/entertainment_news/pull_content";
        switch(itemName){
            case ITEM_SOCIETY:
                break;
            case ITEM_COUNTY:
                address = address.replaceAll("entertainment_news","international_news");
                break;
//            case ITEM_INTERNATION:
//                address = address.replaceAll("social","world");
//                break;
//            case ITEM_FUN:
//                address = address.replaceAll("social","huabian");
//                break;
//            case ITEM_SPORT:
//                address = address.replaceAll("social","tiyu");
//                break;
//            case ITEM_NBA:
//                address = address.replaceAll("social","nba");
//                break;
//            case ITEM_FOOTBALL:
//                address = address.replaceAll("social","football");
//                break;
//            case ITEM_TECHNOLOGY:
//                address = address.replaceAll("social","keji");
//                break;
//            case ITEM_WORK:
//                address = address.replaceAll("social","startup");
//                break;
//            case ITEM_APPLE:
//                address = address.replaceAll("social","apple");
//                break;
//            case ITEM_WAR:
//                address = address.replaceAll("social","military");
//                break;
//            case ITEM_INTERNET:
//                address = address.replaceAll("social","mobile");
//                break;
//            case ITEM_TREVAL:
//                address = address.replaceAll("social","travel");
//                break;
//            case ITEM_HEALTH:
//                address = address.replaceAll("social","health");
//                break;
//            case ITEM_STRANGE:
//                address = address.replaceAll("social","qiwen");
//                break;
//            case ITEM_LOOKER:
//                address = address.replaceAll("social","meinv");
//                break;
//            case ITEM_VR:
//                address = address.replaceAll("social","vr");
//                break;
//            case ITEM_IT:
//                address = address.replaceAll("social","it");
//                break;
            default:
        }
        return address;
    }

    /**
     * 通过 actionbar.getTitle() 的参数，返回对应的 ItemName
     */
    private int parseString(String text){
        if (text.equals("社会新闻")){
            return ITEM_SOCIETY;
        }
        if (text.equals("国内新闻")){
            return ITEM_COUNTY;
        }
//        if (text.equals("国际新闻")){
//            return ITEM_INTERNATION;
//        }
//        if (text.equals("娱乐新闻")){
//            return ITEM_FUN;
//        }
//        if (text.equals("体育新闻")){
//            return ITEM_SPORT;
//        }
//        if (text.equals("NBA新闻")){
//            return ITEM_NBA;
//        }
//        if (text.equals("足球新闻")){
//            return ITEM_FOOTBALL;
//        }
//        if (text.equals("科技新闻")){
//            return ITEM_TECHNOLOGY;
//        }
//        if (text.equals("创业新闻")){
//            return ITEM_WORK;
//        }
//        if (text.equals("苹果新闻")){
//            return ITEM_APPLE;
//        }
//        if (text.equals("军事新闻")){
//            return ITEM_WAR;
//        }
//        if (text.equals("移动互联")){
//            return ITEM_INTERNET;
//        }
//        if (text.equals("旅游资讯")){
//            return ITEM_TREVAL;
//        }
//        if (text.equals("健康知识")){
//            return ITEM_HEALTH;
//        }
//        if (text.equals("奇闻异事")){
//            return ITEM_STRANGE;
//        }
//        if (text.equals("美女图片")){
//            return ITEM_LOOKER;
//        }
//        if (text.equals("VR科技")){
//            return ITEM_VR;
//        }
//        if (text.equals("IT资讯")){
//            return ITEM_IT;
//        }
        return ITEM_SOCIETY;
    }
}