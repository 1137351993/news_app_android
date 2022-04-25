package com.example.gotitapplication.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gotitapplication.ContentActivity;
import com.example.gotitapplication.R;
import com.example.gotitapplication.gson.News;
import com.example.gotitapplication.gson.NewsList;
import com.example.gotitapplication.login.login;
import com.example.gotitapplication.util.HttpUtil;
import com.example.gotitapplication.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.gotitapplication.MainActivity2.account;

public class TabFragment extends Fragment {

    private static final int ITEM_SOCIETY = 1;
    private static final int ITEM_COUNTY = 2;
    private static final int ITEM_INTERNATION = 3;
    private static final int ITEM_FUN = 4;
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


    private List<Title> titleList = new ArrayList<Title>();
    private ListView listView;
    private TitleAdapter adapter;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;

    private int itemName;
    private String mTitle;

    //这个构造方法是便于各导航同时调用一个fragment
    public TabFragment(String title) {
        mTitle = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_200));
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new TitleAdapter(getContext(), R.layout.list_view_item, titleList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(getContext(), ContentActivity.class);

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Title title = titleList.get(position);
                intent.putExtra("itemName",itemName);
                intent.putExtra("id", title.getId());
                intent.putExtra("account", account);
                startActivity(intent);
            }
        });

        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                itemName = parseString(mTitle);
                Toast toast= Toast.makeText(view.getContext(), "temp:"+mTitle, Toast.LENGTH_SHORT);
                toast.show();
                if(itemName!=0)
                    pull();
                else{
                    pull_attention();
                }
            }
        });

        refreshLayout.setRefreshing(true);
        itemName = parseString(mTitle);
        Toast toast= Toast.makeText(view.getContext(), "temp:"+mTitle, Toast.LENGTH_SHORT);
        toast.show();
        if(itemName!=0)
            pull();
        else{

            pull_attention();
        }

        return view;
    }

//    public void requestNew(int itemName) {
//
//        // 根据返回到的 URL 链接进行申请和返回数据
//        String address = response(itemName);    // key
//        HttpUtil.sendOkHttpRequest(address, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getContext(), "新闻加载失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseText = response.body().string();
//                final NewsList newlist = Utility.parseJsonWithGson(responseText);
//                final int code = newlist.code;
//                final String msg = newlist.msg;
//                if (code == 200) {
//                    titleList.clear();
//                    for (News news : newlist.newsList) {
//                        Title title = new Title(news.title, news.description, news.picUrl, news.url);
//                        titleList.add(title);
//                    }
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                            listView.setSelection(0);
//                            refreshLayout.setRefreshing(false);
//                        }
//
//                        ;
//                    });
//                } else {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "数据错误返回", Toast.LENGTH_SHORT).show();
//                            refreshLayout.setRefreshing(false);
//                        }
//                    });
//                }
//
//
//            }
//        });
//

  //  }

    /**
     * 输入不同的类型选项，返回对应的 URL 链接
     */
    private String response(int itemName){
        String address = "http://10.0.2.2:8989/entertainment_news/pull";
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
        else if (text.equals("国内新闻")){
            return ITEM_COUNTY;
        }
        if (text.equals("国际新闻")){
            return ITEM_INTERNATION;
        }
        if (text.equals("娱乐新闻")){
            return ITEM_FUN;
        }
        else{
            return 0;
        }
//
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
    }

    private void pull(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String url = response(itemName);
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("page","0");
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url(url) //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    titleList.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id=jsonObject.getString("id");
                        String title=jsonObject.getString("title");
                        String description=jsonObject.getString("source");
                        String imageurl=jsonObject.getString("img_url_2");

                        Title title1 = new Title(id, title, description, imageurl);
                        titleList.add(title1);
                    }
                    getActivity().runOnUiThread(new Runnable() {
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

    private void pull_attention(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("name",mTitle);
                    params.add("account",account);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/entertain_news/compare") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    titleList.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id=jsonObject.getString("id");
                        String title=jsonObject.getString("title");
                        String description=jsonObject.getString("source");
                        String imageurl=jsonObject.getString("img_url_2");

                        Title title1 = new Title(id, title, description, imageurl);
                        titleList.add(title1);
                    }
                    getActivity().runOnUiThread(new Runnable() {
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
}