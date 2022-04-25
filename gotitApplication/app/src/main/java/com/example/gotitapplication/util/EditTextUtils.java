package com.example.gotitapplication.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/*
android中EditText点击右边叉叉按钮(或图片)清除输入框内的文字的实现
参考博客：https://blog.csdn.net/JWfine/article/details/52416337
 */
public class EditTextUtils {
    public static void clearButtonListener(final EditText et, final View view) {
        // 取得et中的文字
        String etInputString = et.getText().toString();
        // 根据et中是否有文字进行X可见或不可见的判断
        if (TextUtils.isEmpty(etInputString)) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        //点击X时使et中的内容为空
        view.setOnClickListener(v -> {
            et.setText("");
            et.requestFocusFromTouch();
        });
        //对et的输入状态进行监听
        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
