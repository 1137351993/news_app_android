package com.example.gotitapplication.home;

import static android.content.ContentValues.TAG;
import static com.example.gotitapplication.MainActivity2.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.gotitapplication.R;
import com.example.gotitapplication.attention.GroupBean;
import com.example.gotitapplication.login.login;
import com.example.gotitapplication.news_list;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment{
    private ViewPager pager;
    private FragmentAdapter fragmentAdapter;
    private List<TabFragment> fragmentList;
    private TabLayout tabLayout;
    private ImageButton imageButton;
    private EditText editText;
    private List<String> mTitles;
    private String [] title={"运动新闻","娱乐新闻","国际新闻","军事新闻","数码新闻","眼界新闻","财经新闻"};
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.home_fragment,container,false);
        pager=view.findViewById(R.id.page);
        tabLayout=view.findViewById(R.id.tab_layout);
        imageButton=(ImageButton) view.findViewById(R.id.menu_add);
        editText=(EditText) view.findViewById(R.id.et_search_text);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.w(TAG, "run:ulawhedaljsbcalsbcliwyehfljdksbcxhuchzlkjcxiiiiiiii "+key);
                if(editText.getText().toString().equals("")){
                    Toast toast= Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    Intent intent = new Intent(getActivity(), news_list.class);
                    intent.putExtra("key", editText.getText().toString());
                    intent.putExtra("operationName", 4);
                    intent.putExtra("account", account);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        pull_home_package();
        fragmentList=new ArrayList<>();
        mTitles=new ArrayList<>();
        for(int i=0;i<title.length;i++){
            mTitles.add(title[i]);
            fragmentList.add(new TabFragment(title[i]));
        }

        fragmentAdapter=new FragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList,mTitles);
        pager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(pager);//与ViewPage建立关系
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
                            .url("http://123.56.220.66:8989/attention/pull_home_package") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title_name=jsonObject.getString("name");
                        title = insert(title, title_name);
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

    // 往字符串数组追加新数据
    private static String[] insert(String[] arr, String... str) {
        int size = arr.length; // 获取原数组长度
        int newSize = size + str.length; // 原数组长度加上追加的数据的总长度

        // 新建临时字符串数组
        String[] tmp = new String[newSize];
        // 先遍历将原来的字符串数组数据添加到临时字符串数组
        for (int i = 0; i < size; i++) {
            tmp[i] = arr[i];
        }
        // 在末尾添加上需要追加的数据
        for (int i = size; i < newSize; i++) {
            tmp[i] = str[i - size];
        }
        return tmp; // 返回拼接完成的字符串数组
    }
}