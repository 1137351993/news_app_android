package com.example.gotitapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.gotitapplication.home.HomeFragment;
import com.example.gotitapplication.users.PersonalFragment;

public class MainActivity2 extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioButton homeRb, phoneRb, personalRb;
    private RadioGroup mRadioGroup;
    private HomeFragment mHomeFragment;
    private CommunityFragment mCommunityFragment;
    private PersonalFragment mPersonalFragment;

    public static String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_main);
        initView();
        Intent intent=getIntent();
        account = intent.getStringExtra("account");
    }

    private void initView() {
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        homeRb = findViewById(R.id.rd_home);
//        phoneRb = findViewById(R.id.rd_community);
        personalRb = findViewById(R.id.rd_personal);
        homeRb.setChecked(true);

        /**图片的优化，其他三个图片做类似处理
         * 底部导航的时候会发生图片的颜色变化，所以radiobutton中的照片不是一张，而是引用了自定义的选择器照片
         * 本来使用的是getResources.getDrawable,不过已经过时，所以使用ContextCompat
         */
        Drawable home = ContextCompat.getDrawable(this, R.drawable.selector_home_drawable);
        /**
         *  当这个图片被绘制时，给他绑定一个矩形规定这个矩形
         *  参数前两个对应图片相对于左上角的新位置，后两个为图片的长宽
         */
        home.setBounds(0, 0, 80, 80);
        /**
         *   设置图片在文字的哪个方向,分别对应左，上，右，下
         */

        homeRb.setCompoundDrawables(null, home, null, null);

//        Drawable phone = ContextCompat.getDrawable(this, R.drawable.selector_community_drawable);
//        phone.setBounds(0, 0, 80, 80);
//        phoneRb.setCompoundDrawables(null, phone, null, null);

        Drawable personal = ContextCompat.getDrawable(this, R.drawable.selector_personal_drawable);
        personal.setBounds(0, 0, 80, 80);
        personalRb.setCompoundDrawables(null, personal, null, null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (checkedId) {
            case R.id.rd_home:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.fragment_container, mHomeFragment);
                } else {
                    transaction.show(mHomeFragment);
                }
                break;
//            case R.id.rd_community:
//                if (mCommunityFragment == null) {
//                    mCommunityFragment = new CommunityFragment();
//                    transaction.add(R.id.fragment_container, mCommunityFragment);
//                } else {
//                    transaction.show(mCommunityFragment);
//                }
//                break;
            case R.id.rd_personal:
                if (mPersonalFragment == null) {
                    mPersonalFragment = new PersonalFragment();
                    transaction.add(R.id.fragment_container,  mPersonalFragment);
                } else {
                    transaction.show( mPersonalFragment);
                }
                break;
        }
        transaction.commit();
    }

    public void hideAllFragment(FragmentTransaction transaction){
        if(mHomeFragment!=null){
            transaction.hide(mHomeFragment);
        }
//        if(mCommunityFragment!=null){
//            transaction.hide(mCommunityFragment);
//        }
        if(mPersonalFragment!=null){
            transaction.hide(mPersonalFragment);
        }
    }


}