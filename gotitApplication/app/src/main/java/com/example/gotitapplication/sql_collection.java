package com.example.gotitapplication;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class sql_collection {

    private String package_name;
    private int package_visual,package_id;

    public void package_sql(String package_name, int package_visual, int package_id){
        this.package_name=package_name;
        this.package_visual=package_visual;
        this.package_id=package_id;
    }
    public void package_sql(int package_id){
        this.package_id=package_id;
    }

    public void set_package(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("name",package_name);
                    params.add("visual",""+package_visual);
                    params.add("package_id",""+package_id);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/attention/set_package") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    client.newCall(request).execute(); //执行发送指令
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

    public void delete_package(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() { //类型2——Param型
                try {
                    FormBody.Builder params = new FormBody.Builder();
                    params.add("package_id",""+package_id);
                    OkHttpClient client = new OkHttpClient(); //创建http客户端
                    Request request = new Request.Builder()
                            .url("http://123.56.220.66:8989/attention/delete_package") //后端请求接口的地址
                            .post(params.build())
                            .build(); //创建http请求
                    client.newCall(request).execute(); //执行发送指令
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
