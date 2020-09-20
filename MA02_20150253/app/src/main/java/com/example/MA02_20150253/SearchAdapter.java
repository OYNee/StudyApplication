package com.example.MA02_20150253;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<MySearchDto> list;
    private ViewHolder viewHolder = null;

    public SearchAdapter(Context context, int layout, ArrayList<MySearchDto> list) {
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
    public MySearchDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    public void setList(ArrayList<MySearchDto> list) {
        this.list = list;
    }

    public void clear() {
        this.list = new ArrayList<MySearchDto>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {     //dto의 값을 각 뷰에 알맞게 연결 후 뷰를 리턴.

        View view = convertView;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(layout, parent, false);
            viewHolder.tvTitle = (TextView)view.findViewById(R.id.tvTitleMonthly);
            viewHolder.tvDescription = (TextView)view.findViewById(R.id.tvDescription);
            viewHolder.tvLink = (TextView)view.findViewById(R.id.tvLink);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        MySearchDto dto = list.get(position);

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvDescription.setText(dto.getDescription());
        viewHolder.tvLink.setText(dto.getLink());

        return view;
    }


    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvDescription = null;
        public TextView tvLink = null;
    }
}
