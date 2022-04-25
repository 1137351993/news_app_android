package com.example.gotitapplication.users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.gotitapplication.R;
import com.example.gotitapplication.attention.attention;
import com.example.gotitapplication.login.login;
import com.example.gotitapplication.news_list;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.gotitapplication.MainActivity2.account;

public class PersonalFragment extends Fragment {
    private Button button_exit, button_history, button_attention, button_message;
    private ImageView user_image;
    private String user_n, user_p, user_name;
    private TextView textView;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.person_layout, container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_exit=(Button) view.findViewById(R.id.tuichu);//退出登陆
        button_history=(Button) view.findViewById(R.id.history_button);
        button_attention=(Button) view.findViewById(R.id.attention_button);
        button_message=(Button) view.findViewById(R.id.editor_message);
        user_image=(ImageView) view.findViewById(R.id.userpic);

        init();
        textView=(TextView)view.findViewById(R.id.user_name);
        textView.setText(user_name);

        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), login.class);
                startActivity(intent);
            }
        });

        //历史记录
        button_history.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), news_list.class);
                intent.putExtra("account", account);
                intent.putExtra("operationName", 1);
                startActivity(intent);
            }
        });

        //关注
        button_attention.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), attention.class);
                intent.putExtra("account", account);
                startActivity(intent);
            }
        });

        button_message.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), PersonInfo.class);
                intent.putExtra("account", account);
                startActivity(intent);
            }
        });

        //Glide.with(this).load(user_p).apply(RequestOptions.bitmapTransform(new CircleCrop()))
        //        .into(user_image);
    }
    private void init(){
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