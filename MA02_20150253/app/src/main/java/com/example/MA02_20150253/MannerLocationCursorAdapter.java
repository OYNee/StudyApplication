package com.example.MA02_20150253;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MannerLocationCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    Cursor cursor;

    public MannerLocationCursorAdapter(Context context, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tvLocT = (TextView) view.findViewById(R.id.tvLocTitle);
        TextView tvLocA = (TextView) view.findViewById(R.id.tvLocAddr);
        tvLocT.setText(cursor.getString(1));
        tvLocA.setText(cursor.getString(2));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View listItemLayout = inflater.inflate(R.layout.location_item, viewGroup, false);
        return listItemLayout;
    }
}