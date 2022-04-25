package com.example.gotitapplication.attention;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gotitapplication.R;
import com.example.gotitapplication.home.Title;
import com.example.gotitapplication.home.TitleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MsExpandableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<GroupBean> gData;
    private ArrayList<ArrayList<Title>> titleList;
    private Context mContext;
    private TitleAdapter adapter;

    public MsExpandableListAdapter(ArrayList<GroupBean> gData,ArrayList<ArrayList<Title>> titleList, Context mContext) {
        this.gData = gData;
        this.titleList = titleList;
        this.mContext = mContext;
    }

    @Override
    public int getGroupCount() {
        return gData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return titleList.get(groupPosition).size();
    }

    @Override
    public GroupBean getGroup(int groupPosition) {
        return gData.get(groupPosition);
    }

    @Override
    public Title getChild(int groupPosition, int childPosition) {
        return titleList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolderGroup groupHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.hero_group_item, parent, false);
            groupHolder = new ViewHolderGroup();
            groupHolder.group_name = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.group_name.setText(gData.get(groupPosition).getName());
        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_view_item, parent, false);
            itemHolder = new ViewHolderItem();
            itemHolder.titleText = (TextView)convertView.findViewById(R.id.title_text);
            itemHolder.titlePic = (ImageView) convertView.findViewById(R.id.title_pic);
            itemHolder.titleDescr = (TextView)convertView.findViewById(R.id.descr_text);
            convertView.setTag(itemHolder);
        }else{
            itemHolder = (ViewHolderItem) convertView.getTag();
        }

        Glide.with(this.mContext).load(titleList.get(groupPosition).get(childPosition).getImageUrl()).into(itemHolder.titlePic);
        itemHolder.titleText.setText(titleList.get(groupPosition).get(childPosition).getTitle());
        itemHolder.titleDescr.setText(titleList.get(groupPosition).get(childPosition).getDescr());
        return convertView;
    }

    //设置子列表是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolderGroup{
        private TextView group_name;
    }

    private static class ViewHolderItem{
        private TextView titleText;
        private TextView titleDescr;
        private ImageView titlePic;
    }

}