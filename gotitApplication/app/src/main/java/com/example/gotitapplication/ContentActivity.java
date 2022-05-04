package com.example.gotitapplication;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import static com.example.gotitapplication.MainActivity2.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
    private String[] attention_package;
    private int[] package_id;
    private int select_size=0;

    private int itemName;

    private TextView title_view, source_view, datetime_view;
    private Button button_attention, button_comment;
    private EditText comment;
    private AlertDialog dialog;

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
                pull_home_package();
                System.out.println(attention_package[1]);
                AlertDialog.Builder builder = new AlertDialog.Builder(ContentActivity.this)//设置单选框列表
                        .setTitle("请选择收藏夹")   //设置标题
                        .setSingleChoiceItems(attention_package, select_size, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                select_size=i; //在OnClick方法中得到被点击的序号 i
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//在对话框中设置“确定”按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //为TextView设置在单选对话框中选择的字体大小
//                                attention_package[select_size].toString();
                                push_attention();
                                //设置好字体大小后关闭单选对话框
                                dialog.dismiss();
                                Toast toast= Toast.makeText(ContentActivity.this, "关注成功"+itemName, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//在对话框中设置”取消按钮“
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();


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
                            .url("http://123.56.220.66:8989/comment_news/init") //后端请求接口的地址
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
                            .url("http://123.56.220.66:8989/users/pull") //后端请求接口的地址
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
                            .url("http://123.56.220.66:8989/comment_news/push") //后端请求接口的地址
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
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("id",id);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/entertainment_news/pull_content") //后端请求接口的地址
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
                            .url("http://123.56.220.66:8989/history/push") //后端请求接口的地址
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
                    String json = "{\"id\":\""+id+"\",\"account\":\""+account+"\",\"package_id\":"+package_id[select_size]+"}";
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/attention/push") //后端请求接口的地址
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

    private void pull_home_package(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("account",account);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/attention/pull_all_package") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    package_id = new int[jsonArray.length()];
                    attention_package = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        package_id[i] = jsonObject.getInt("package_id");
                        attention_package[i] = jsonObject.getString("name");
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

}