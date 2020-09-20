package com.example.MA02_20150253;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MannerLocAdapter extends BaseAdapter{
    public static final String TAG = "MyDto";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MyLocation> list;
    private ViewHolder viewHolder = null;

    public MannerLocAdapter(Context context, int layout, ArrayList<MyLocation> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MyLocation getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {     //dto의 값을 각 뷰에 알맞게 연결 후 뷰를 리턴.

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(layout, parent, false);
            viewHolder.tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            viewHolder.tvAddress = (TextView)view.findViewById(R.id.tvAddress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MyLocation dto = list.get(position);

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvAddress.setText(dto.getAddress());

        return view;
    }


    public void setList(ArrayList<MyLocation> list) {
        this.list = list;
    }

    public void clear() {
        this.list = new ArrayList<MyLocation>();
    }

    // ViewHolder 는 매번 findViewById 를 수행하지 않도록 도와주는 클래스이므로 생략해도 무방

    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvCategory = null;
        public TextView tvAddress = null;
    }
}
