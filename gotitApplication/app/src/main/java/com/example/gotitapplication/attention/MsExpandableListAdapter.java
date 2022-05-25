package com.example.gotitapplication.attention;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.gotitapplication.R;
import com.example.gotitapplication.home.Title;
import com.example.gotitapplication.home.TitleAdapter;
import com.example.gotitapplication.sql_collection;

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
    private int visual_key=0;

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
            groupHolder.attention_package_edit = (TextView) convertView.findViewById(R.id.attention_package_edit);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.group_name.setText(gData.get(groupPosition).getName());

        groupHolder.attention_package_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder customizeDialog = new AlertDialog.Builder(mContext);
                final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_customize,null);
                customizeDialog.setTitle("收藏夹编辑");
                customizeDialog.setView(dialogView);

                EditText edit_text = (EditText) dialogView.findViewById(R.id.set_package_name);
                edit_text.setText(gData.get(groupPosition).getName());

                RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.rg);
                RadioButton radioButton;
                if(gData.get(groupPosition).getVisual()==1)
                    radioButton = (RadioButton) dialogView.findViewById(R.id.yes);
                else
                    radioButton = (RadioButton) dialogView.findViewById(R.id.no);
                radioGroup.check(radioButton.getId());
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        visual_key=checkedId == R.id.yes ? 1 : 0;
                    }
                });

                customizeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql_collection sql = new sql_collection();
                        sql.package_sql(edit_text.getText().toString(),
                                visual_key, gData.get(groupPosition).getPackage_id());
                        sql.set_package();
                        gData.get(groupPosition).setName(edit_text.getText().toString());
                        gData.get(groupPosition).setVisual(visual_key);
                        Toast.makeText(mContext, "编辑成功", Toast.LENGTH_SHORT).show();
                    }
                });
                customizeDialog.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sql_collection sql = new sql_collection();
                        sql.package_sql(gData.get(groupPosition).getPackage_id());
                        sql.delete_package();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                });
                customizeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                customizeDialog.show();
            }
        });

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
        private TextView attention_package_edit;
    }

    private static class ViewHolderItem{
        private TextView titleText;
        private TextView titleDescr;
        private ImageView titlePic;
    }

}