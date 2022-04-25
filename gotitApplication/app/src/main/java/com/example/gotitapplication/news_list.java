package com.example.gotitapplication;

import static android.content.ContentValues.TAG;
import static com.example.gotitapplication.MainActivity2.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.gotitapplication.home.Title;
import com.example.gotitapplication.home.TitleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class news_list extends Activity {
    private List<Title> titleList = new ArrayList<Title>();
    private ListView listView;
    private TitleAdapter adapter;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;

    private static final int ITEM_SOCIETY = 1;
    private static final int ITEM_COUNTY = 2;
//    private static final int ITEM_INTERNATION = 3;

    private static final int ITEM_LISHI = 1;
    private static final int ITEM_SHOUCANG = 2;
//    private static final int ITEM_GUANZHU = 3;

    private int itemName;
    private int operationName;
    private String account;
    private String id;
    private String key;
//    private String mTitle;
//
//    //这个构造方法是便于各导航同时调用一个fragment
//    public TabFragment(String title) {
//        mTitle = title;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_list);

        Intent intent=getIntent();
        operationName = intent.getIntExtra("operationName", 0);
        account = intent.getStringExtra("account");
        key = intent.getStringExtra("key");

//        toolbar=(Toolbar)findViewById(R.id.news_list_title);
//        toolbar.setTitle("我的历史记录");

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.news_swipe_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_200));
        listView = (ListView) findViewById(R.id.news_list_view);
        adapter = new TitleAdapter(this, R.layout.list_view_item, titleList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(news_list.this, ContentActivity.class);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Title title = titleList.get(position);
                intent.putExtra("itemName",itemName);
                intent.putExtra("id", title.getId());
                intent.putExtra("account", account);
                startActivity(intent);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.news_drawer_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                titleList.clear();
                refreshLayout.setRefreshing(true);
                if(operationName==4){
                    pull_select();
                }else{
                    pull_history_news();
                }
            }
        });

        refreshLayout.setRefreshing(true);
        if(operationName==4){
            pull_select();
        }else{
            pull_history_news();
        }
    }

    private void pull_select(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("key",key);
                    //1Log.w(TAG, "run:ulawhedaljsbcalsbcliwyehfljdksbcxhuchzlkjcxiiiiiiii "+key);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(news_list.this,key, Toast.LENGTH_LONG).show();
                        }
                    });
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/entertainment_news/pull_select") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id=jsonObject.getString("id");
                        String title=jsonObject.getString("title");
                        String description=jsonObject.getString("source");
                        String imageurl=jsonObject.getString("img_url_2");

                        Title title1 = new Title(id, title, description, imageurl);
                        titleList.add(title1);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            listView.setSelection(0);
                            refreshLayout.setRefreshing(false);
                        };
                    });
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

    private void pull_history_news(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String url = response(operationName);
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("account",account);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url(url) //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id=jsonObject.getString("id");
                        String title=jsonObject.getString("title");
                        String description=jsonObject.getString("source");
                        String imageurl=jsonObject.getString("img_url_2");

                        Title title1 = new Title(id, title, description, imageurl);
                        titleList.add(title1);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            listView.setSelection(0);
                            refreshLayout.setRefreshing(false);
                        };
                    });
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
        String address = "http://10.0.2.2:8989/entertainment_news/pull_history";
        switch(itemName){
            case ITEM_SOCIETY:
                break;
            case ITEM_COUNTY:
                address = address.replaceAll("pull_history","pull_attention");
                break;
//            case ITEM_INTERNATION:
//                address = address.replaceAll("social","world");
//                break;
            default:
        }
        return address;
    }


}
