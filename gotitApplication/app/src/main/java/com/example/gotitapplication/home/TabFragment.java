package com.example.gotitapplication.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    private static final int ITEM_SPORT = 0;            //运动
    private static final int ITEM_ENTERTAINMENT = 1;    //娱乐
    private static final int ITEM_INTERNATION = 2;      //国际
    private static final int ITEM_WAR= 3;               //军事
    private static final int ITEM_TECHNOLOGY = 4;       //数码
    private static final int ITEM_LOOKER= 5;            //眼界
    private static final int ITEM_FINANCE = 6;          //财经

    private List<Title> titleList = new ArrayList<Title>();
    private ListView listView;
    private TitleAdapter adapter;
    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private static int page=0;

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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount && firstVisibleItem!=0) {
                    Log.d("ListView", "##### 滚动到底部 ######");
                    if(!(titleList.size()<10)) {
                        int position = listView.getFirstVisiblePosition();
                        int y = listView.getChildAt(0).getTop();
                        page += 10;
                        if (itemName != -1)
                            pull();
                        else {
                            pull_attention();
                        }
                        System.out.println("位置：" + position);
                        adapter = new TitleAdapter(getContext(), R.layout.list_view_item, titleList);
                        listView.setAdapter(adapter);
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                listView.requestFocusFromTouch();//获取焦点
                                listView.setSelectionFromTop(listView.getHeaderViewsCount() + position, y);//10是你需要定位的位置
                            }
                        });
                        //listView.setSelectionFromTop(position, y);
                    }
                }
            }
        });

        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                itemName = parseString(mTitle);
                page+=10;
                titleList.clear();
                if(itemName!=-1)
                    pull();
                else{
                    pull_attention();
                }
                adapter = new TitleAdapter(getContext(), R.layout.list_view_item, titleList);
                listView.setAdapter(adapter);
            }
        });

        //refreshLayout.setRefreshing(true);
        itemName = parseString(mTitle);
//        Toast toast= Toast.makeText(view.getContext(), "temp:"+mTitle, Toast.LENGTH_SHORT);
//        toast.show();
        if(itemName!=-1)
            pull();
        else{

            pull_attention();
        }

        return view;
    }

    /**
     * 通过 actionbar.getTitle() 的参数，返回对应的 ItemName
     */
    private int parseString(String text){
        if (text.equals("运动新闻")){
            return ITEM_SPORT;
        }else if (text.equals("娱乐新闻")){
            return ITEM_ENTERTAINMENT;
        }else if (text.equals("国际新闻")){
            return ITEM_INTERNATION;
        }else if (text.equals("军事新闻")){
            return ITEM_WAR;
        }else if (text.equals("数码新闻")){
            return ITEM_TECHNOLOGY;
        }else if (text.equals("眼界新闻")){
            return ITEM_LOOKER;
        }else if (text.equals("财经新闻")){
            return ITEM_FINANCE;
        }else{
            return -1;
        }
    }

    private void pull(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    //System.out.println("页数："+page+"新闻数量"+titleList.size());
                    params.add("page",""+page);
                    params.add("type", ""+itemName);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/entertainment_news/pull") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    //titleList.clear();
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
                            .url("http://123.56.220.66:8989/entertainment_news/compare") //后端请求接口的地址
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