package com.example.gotitapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotitapplication.MainActivity2;
import com.example.gotitapplication.R;
import com.example.gotitapplication.util.EditTextUtils;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {

    private EditText et_qqnum, et_qqpwd;
    private ImageView iv_login, et_delete_num, et_delete_pwd, et_pwd_see;
    private TextView tv_forgetpwd, tv_register, login_text;
    private String qq_numtext, qq_pwdtext, temp;
    private boolean pwdCanSee;
    private boolean check_key;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        findId();
        //QQ账号输入状态监听
        et_qqnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                qq_pwdtext = et_qqpwd.getText().toString().trim();
                qq_numtext = et_qqnum.getText().toString().trim();
                if(!TextUtils.isEmpty(qq_numtext) && !TextUtils.isEmpty(qq_pwdtext)){
                    //如果账号和密码都不为空，打开图片响应事件，并且更换图片
                    iv_login.setEnabled(true);
                    iv_login.setImageResource(R.drawable.go_yes);
                }else {
                    iv_login.setEnabled(false);
                    iv_login.setImageResource(R.drawable.go_no);
                }
            }
        });

        //QQ密码输入状态监听
        et_qqpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                qq_pwdtext = et_qqpwd.getText().toString().trim();
                qq_numtext = et_qqnum.getText().toString().trim();
                if(!TextUtils.isEmpty(qq_numtext) && !TextUtils.isEmpty(qq_pwdtext)){
                    //如果账号和密码都不为空，打开图片响应事件，并且更换图片
                    iv_login.setEnabled(true);
                    iv_login.setImageResource(R.drawable.go_yes);
                }else {
                    iv_login.setEnabled(false);
                    iv_login.setImageResource(R.drawable.go_no);
                }
            }
        });
        //QQ密码输入焦点监听
        et_qqpwd.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if(hasFocus){
                    et_pwd_see.setVisibility(View.VISIBLE);
                }else {
                    et_pwd_see.setVisibility(View.INVISIBLE);
                }
            }

        });

        //密码可见小图标
        pwdCanSee = false;//true密码可见，false密码不可见
        et_pwd_see.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if(pwdCanSee){
                    //设置不可见
                    et_qqpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_pwd_see.setImageResource(R.drawable.et_pwd_no);
                    et_qqpwd.setSelection(et_qqpwd.getText().length());
                    pwdCanSee = false;
                }else {
                    //设置可见
                    et_qqpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_pwd_see.setImageResource(R.drawable.et_pwd_yes);
                    et_qqpwd.setSelection(et_qqpwd.getText().length());
                    pwdCanSee = true;
                }
            }
        });


        //删除小图标
        EditTextUtils.clearButtonListener(et_qqnum, et_delete_num);
        EditTextUtils.clearButtonListener(et_qqpwd, et_delete_pwd);

        //登录
        //iv_login.setClickable(true);
        //setOnClickListener方法会默认把控件的setClickable设置为true。
        //设置login图片无事件响应
        iv_login.setEnabled(false);
        iv_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                qq_numtext = et_qqnum.getText().toString().trim();
                qq_pwdtext = et_qqpwd.getText().toString().trim();
                login_check();

                if(check_key){
                    Toast toast= Toast.makeText(login.this, "lllll", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(login.this, MainActivity2.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                }
                else{
//                    Toast toast= Toast.makeText(login.this, temp, Toast.LENGTH_SHORT);
//                    toast.show();
                    et_qqpwd.setText("");
                    login_text.setVisibility(View.VISIBLE);
                }


            }
        });

        //忘记密码

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });
    }

    public void onRestart() {
        super.onRestart();
        check_key=false;
    }


    private void login_check(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    String json = "{\"account\":\""+qq_numtext+"\",\"password\":\""+qq_pwdtext+ "\"}";
                    temp=json;
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/users/login") //后端请求接口的地址
                            .post(RequestBody.create(MediaType.parse("application/json"),json))
                            .build(); //创建http请求
                    Response response = client.newCall(request).execute(); //执行发送指令
                    //获取后端回复过来的返回值(如果有的话)
                    String responseData = response.body().string(); //获取后端接口返回过来的JSON格式的结果
                    JSONObject jsonObject = new JSONObject(responseData); //将文本格式的JSON转换为JSON数组
                    if(jsonObject != null) {
                        check_key=true;
                        account = jsonObject.getString("account");
                    }
                    else { check_key=false; }
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

    private void findId(){
        et_qqnum = findViewById(R.id.qq_num);
        et_qqpwd = findViewById(R.id.qq_pwd);
        iv_login = findViewById(R.id.qq_login);
        tv_forgetpwd = findViewById(R.id.qq_forgetpwd);
        tv_register = findViewById(R.id.qq_register);
        et_delete_num = findViewById(R.id.iv_et_num_delete);
        et_delete_pwd = findViewById(R.id.iv_et_pwd_delete);
        et_pwd_see = findViewById(R.id.iv_et_pwd_see);
        login_text = findViewById(R.id.login_text);
    }
}
