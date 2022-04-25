package com.example.gotitapplication.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gotitapplication.R;
import com.example.gotitapplication.util.JellyInterpolator;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class signup extends Activity implements View.OnClickListener {

    private TextView mBtnLogin, signup_tip;

    private EditText signup_account, signup_name, signup_psd1, signup_psd2;

    private View progress;

    private View mInputLayout;

    private float mWidth, mHeight;

    private LinearLayout mName, mPsw,uName,mPsw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sign_up);

        initView();
    }

    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.main_btn_signup);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);///////////账号
        uName= (LinearLayout) findViewById(R.id.input_layout_uname);/////////用户名
        mPsw = (LinearLayout) findViewById(R.id.input_layout_psw);/////密码1
        mPsw2 = (LinearLayout) findViewById(R.id.input_layout_psw2);////密码2
        mBtnLogin.setOnClickListener(this);
        signup_account = (EditText) findViewById(R.id.signup_account);
        signup_name = (EditText) findViewById(R.id.signup_name);
        signup_psd1 = (EditText) findViewById(R.id.signup_psd1);
        signup_psd2 = (EditText) findViewById(R.id.signup_psd2);
        signup_tip = (TextView) findViewById(R.id.signup_tip);
    }

    @Override
    public void onClick(View v) {

        if(signup_account.getText().toString().equals("")){
            signup_tip.setText("账号为空");
            signup_tip.setVisibility(View.VISIBLE);
        }
        else if(signup_name.getText().toString().equals("")){
            signup_tip.setText("用户名为空");
            signup_tip.setVisibility(View.VISIBLE);
        }
        else if(signup_psd1.getText().toString().equals("")){
            signup_tip.setText("请输入密码");
            signup_tip.setVisibility(View.VISIBLE);
        }
        else if(signup_psd2.getText().toString().equals("")){
            signup_tip.setText("请重复输入密码");
            signup_tip.setVisibility(View.VISIBLE);
        }
        else if(signup_psd1.getText().toString().equals(signup_psd2.getText().toString())){
            // 计算出控件的高与宽
            mWidth = mBtnLogin.getMeasuredWidth();
            mHeight = mBtnLogin.getMeasuredHeight();
            // 隐藏输入框
            mName.setVisibility(View.INVISIBLE);
            uName.setVisibility(View.INVISIBLE);
            mPsw.setVisibility(View.INVISIBLE);
            mPsw2.setVisibility(View.INVISIBLE);

            inputAnimator(mInputLayout, mWidth, mHeight);
            insert();
        }
        else{
            signup_psd2.setText("");
            signup_tip.setText("两次输入密码不同");
            signup_tip.setVisibility(View.VISIBLE);
        }
    }
//弹出对话框
    private void showNormalDialog1() {
        AlertDialog.Builder dialog = new AlertDialog.Builder (this);
        dialog.setTitle ("提示").setMessage ("注册成功");
        //点击确定就退出程序
        dialog.setPositiveButton ("确定", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
                signup.this.finish ();
            }
        });
        //如果取消，就什么都不做，关闭对话框
        dialog.setNegativeButton ("取消",null);
        dialog.show ();
    }
    //存入数据库
    private void insert(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("account",signup_account.getText().toString()); //添加url参数
                    params.add("password", signup_psd1.getText().toString());
                    params.add("user_name", signup_name.getText().toString());
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8989/users/sign_up") //后端请求接口的地址
                            .post(params.build()).build(); //创建http请求
                    client.newCall(request).execute(); //执行发送指令
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try  {
            thread . join () ;
        }  catch  ( InterruptedException e) {
            e . printStackTrace () ;
        }
    }


    /**
     * 输入框的动画效果
     *
     * @param view
     *            控件
     * @param w
     *            宽
     * @param h
     *            高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(5000);

        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
        animator3.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                showNormalDialog1();


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
    }
}