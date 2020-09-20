package com.example.MA02_20150253;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyStudyListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MyStudyDto> myDataList;
    private LayoutInflater layoutInflater;

    public MyStudyListAdapter(Context context, int layout, ArrayList<MyStudyDto> myDataList) {
        this.context = context;
        this.layout = layout;
        this.myDataList = myDataList;
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return myDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myDataList.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;

        if(convertView==null) {
            convertView = layoutInflater.inflate(layout, parent, false);
        }

        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        img.setImageResource(myDataList.get(position).getImg());

        TextView name = (TextView)convertView.findViewById(R.id.tvName);
        name.setText(myDataList.get(position).getName());

        TextView percentage = (TextView)convertView.findViewById(R.id.tvPercentage);
        percentage.setText(myDataList.get(position).getPercentage());

        return convertView;
    }
}
