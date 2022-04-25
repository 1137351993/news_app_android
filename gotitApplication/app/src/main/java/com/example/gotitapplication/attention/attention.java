package com.example.gotitapplication.attention;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gotitapplication.ContentActivity;
import com.example.gotitapplication.R;
import com.example.gotitapplication.home.Title;
import com.example.gotitapplication.news_list;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class attention extends AppCompatActivity {

    private ArrayList<GroupBean> gData = null;
    private ArrayList<ArrayList<Title>> titleList = null;
    private ArrayList<Title> title_item = null;
    private Context mContext;
    private ExpandableListView lol_hero_list;
    private MsExpandableListAdapter msAdapter = null;
    private String account;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attention_main);

        add=(Button)findViewById(R.id.attention_package_add);

        Intent intent=getIntent();
        account = intent.getStringExtra("account");

        pull_package();

        mContext = attention.this;
        lol_hero_list = (ExpandableListView) findViewById(R.id.lol_hero_list);

        msAdapter = new MsExpandableListAdapter(gData,titleList,mContext);
        lol_hero_list.setAdapter(msAdapter);

        lol_hero_list.expandGroup(0);

        //为列表设置点击事件
        lol_hero_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(mContext, "你点击了：" + titleList.get(groupPosition).get(childPosition).getTitle(), Toast.LENGTH_SHORT).show();
//                return true;
                Intent intent = new Intent(attention.this, ContentActivity.class);
                Title title = titleList.get(groupPosition).get(childPosition);
                intent.putExtra("itemName",1);
                intent.putExtra("id", title.getId());
                intent.putExtra("account", account);
                startActivity(intent);
                return true;
            }
        });

        //添加收藏夹按钮
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(attention.this);
                new AlertDialog.Builder(attention.this).setTitle("请输入收藏夹名称")
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
                                Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
                                et.getText().toString();
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });
    }

    private void pull_package(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("account",account);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/attention/pull_package") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONArray jsonArray = new JSONArray(responseData); //将文本格式的JSON转换为JSON数组
                    gData = new ArrayList<GroupBean>();
                    titleList = new ArrayList<ArrayList<Title>>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        int package_id=jsonObject.getInt("package_id");
                        gData.add(new GroupBean(package_id, name));


                        FormBody.Builder params_item = new FormBody.Builder();
                        params_item.add("package_id",""+package_id);
                        OkHttpClient client_item = new OkHttpClient(); //创建http客户端
                        Request request_item = new Request.Builder()
                                .url("http://123.56.220.66:8989/entertainment_news/pull_attention") //后端请求接口的地址
                                .post(params_item.build())
                                .build(); //创建http请求
                        Response response_item = client_item.newCall(request_item).execute(); //执行发送指令
                        //获取后端回复过来的返回值(如果有的话)
                        String responseData_item = response_item.body().string(); //获取后端接口返回过来的JSON格式的结果
                        JSONArray jsonArray_item = new JSONArray(responseData_item); //将文本格式的JSON转换为JSON数组
                        title_item = new ArrayList<Title>();
                        for(int j=0;j<jsonArray_item.length();j++) {
                            JSONObject jsonObject_item = jsonArray_item.getJSONObject(j);
                            String id=jsonObject_item.getString("id");
                            String title=jsonObject_item.getString("title");
                            String description=jsonObject_item.getString("source");
                            String imageurl=jsonObject_item.getString("img_url_2");
                            title_item.add(new Title(id, title, description, imageurl));
                            titleList.add(title_item);
                        }

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