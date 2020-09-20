package com.example.MA02_20150253;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyDailyStudyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MyDailyStudyDto> myDataList;
    private LayoutInflater layoutInflater;

    public MyDailyStudyAdapter(Context context, int layout, ArrayList<MyDailyStudyDto> myDataList) {
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

        ImageView img = (ImageView) convertView.findViewById(R.id.imgDaily);
        img.setImageResource(myDataList.get(position).getImg());

        TextView title = (TextView)convertView.findViewById(R.id.tvTitleDaily);
        title.setText(myDataList.get(position).getTitle());

        return convertView;
    }
}
