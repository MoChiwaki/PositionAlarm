package com.example.yukiko.positionalarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Cursorからフィールドの値を取得して
 * ListViewの各行に編集する
 */
public class ListAdapter extends CursorAdapter {

    public ListAdapter(Context context, Cursor cursor, int flag) {
        super(context,cursor,flag);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
        String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
        TextView tv_id = (TextView)view.findViewById(R.id._id);
        TextView tv_date = (TextView)view.findViewById(R.id.date);
        TextView tv_place = (TextView)view.findViewById(R.id.address);

        tv_id.setText(String.valueOf(id));
        tv_date.setText(String.valueOf(date));
        tv_place.setText(String.valueOf(address));


    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row, null);

        return view;

    }

}
