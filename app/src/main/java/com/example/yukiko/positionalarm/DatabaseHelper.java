package com.example.yukiko.positionalarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * MapRecordテーブルの
 * DB、テーブル作成ヘルパークラス
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "maprecord.db";
    private static final int DBVERSION = 1;
    public static final String TABLE_MAPRECORD = "maprecord";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE ="date";
//    public static final String COLUMN_ELAPSEDTIME = "elTime";
//    public static final String COLUMN_DISTANCE = "distance";
//    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_CONTENT = "content";
    private static final String CREATE_TABLE_SQL =
            "create table " + TABLE_MAPRECORD + " "
                    + "(" + COLUMN_ID + " integer primary key autoincrement,"
                    + COLUMN_DATE + " text not null,"
                    + COLUMN_LATITUDE + " text null, "
                    + COLUMN_LONGITUDE + " text null, "
                    + COLUMN_ADDRESS + " text null, "
                    + COLUMN_CONTENT + " text default null) ";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
