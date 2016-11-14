package com.example.yukiko.positionalarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class MapsActivity extends FragmentActivity
        implements ConnectionCallbacks, OnConnectionFailedListener,
        LocationListener, LoaderManager.LoaderCallbacks<Address>, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private static final int ADDRESSLOADER_ID = 0;

    private GoogleMap mMap; // Might be null if Google Play services APK is nßot available.
    private GoogleApiClient mGoogleApiClient;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(3000) // 更新間隔 INTERVALは500ミリ秒
            .setFastestInterval(200000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // 位置情報取得要求の優先順位

    // 位置が変わったとき通知を受け取る
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private List<LatLng> mMapList = new ArrayList<LatLng>();
    private long mStartTimeMillis;

    private DatabaseHelper mDbHelper;
    private boolean mAsked = false;
    public String currentPlace = "";
    private int notificationId = 0;


    // メンバ変数の保存
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ASKED", mAsked);
    }

    // メンバ変数の復帰
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAsked = savedInstanceState.getBoolean("ASKED");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Google playサービスに入る イベントを取得
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mMap == null) {
//            getMap()つかえないじゃーーん！
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(this);
            SupportMapFragment mFragmentMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mFragmentMap.getMapAsync(this);
        }
        mDbHelper = new DatabaseHelper(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // google playサービスに接続
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            // 位置の更新をリクエストする
            fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);
        } catch (SecurityException e) {
            e.getMessage();
        }
    }

    /**
     * 地図の移動、住所取得など
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(19)
                .bearing(0).build();
        // 地図の中心を緯度経度に動かす
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));

        // マーカー設定
        mMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);

        // ドロイド君のマーカー
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        options.icon(icon);
        mMap.addMarker(options);

        Bundle args = new Bundle();
        args.putDouble("lat", location.getLatitude());
        args.putDouble("lon", location.getLongitude());

        Log.d("Log: ", "緯度経度　" + args.toString());

        // 緯度経度から住所取得
        getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        // 位置更新後などに接続を切る
        mGoogleApiClient.disconnect();
    }


    // 位置情報更新をやめる
    protected void stopLocationUpdates() {
        fusedLocationProviderApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public Loader<Address> onCreateLoader(int id, Bundle args) {
        double lat = args.getDouble("lat");
        double lon = args.getDouble("lon");
        return new AddressTaskLoader(this, lat, lon);
    }

    @Override
    public void onLoadFinished(Loader<Address> loader, Address result) {
        if(result != null){
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < result.getMaxAddressLineIndex() + 1; i++) {
                String item = result.getAddressLine(i);
                if(item == null) {
                    break;
                }
                sb.append(item);
            }
            // 返ってきた住所を画面に表示
            TextView address = (TextView)findViewById(R.id.address);
            address.setText(sb.toString());
            Log.d("今の住所", " : " + sb.toString());
            currentPlace = sb.toString();
            getAllAddress();
        }
    }

    @Override
    public void onLoaderReset(Loader<Address> loader) {

    }

    /**
     * レコード登録(コンテンツプロバイダー使う)
     */
    public void saveJogViaCTP(String contentText) {
        Date date = new Date();
        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
        TextView textAddress = (TextView)findViewById(R.id.address);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ADDRESS, textAddress.getText().toString());
        if(contentText != null) {
            values.put(DatabaseHelper.COLUMN_CONTENT, contentText);
        } else {
            values.put(DatabaseHelper.COLUMN_CONTENT, "");
        }
        Uri uri = getContentResolver().insert(MapRecordContentProvider.CONTENT_URI,values);
        Toast.makeText(this, "データを保存しました", Toast.LENGTH_SHORT).show();
    }

    /**
     * レコード登録(コンテンツプロバイダー使わない)
     */
    public void saveJog() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String strDate = new SimpleDateFormat("yyyy/MM/dd").format(mStartTimeMillis);
        TextView txtAddress = (TextView)findViewById(R.id.address);
        EditText registText = (EditText)findViewById(R.id.contentText);

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, strDate);
        values.put(DatabaseHelper.COLUMN_ADDRESS, txtAddress.getText().toString());
        values.put(DatabaseHelper.COLUMN_CONTENT, registText.getText().toString());

        try {
            db.insert(DatabaseHelper.TABLE_MAPRECORD, null, values);
        } catch (Exception e) {
            Toast.makeText(this, "データの保存に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);
    }

    /**
     * 長押しされたらダイアログを表示
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        // カスタムビューを設定
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog,
                (ViewGroup)findViewById(R.id.contentText));

        // アラートダイアログ を生成
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("登録しますか？");
        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OK ボタンクリック処理
                EditText contentText = (EditText) layout.findViewById(R.id.contentText);
                String str = contentText.getText().toString();
                contentText.setText(str);

                saveJogViaCTP(str);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理
            }
        });

        // 表示
        builder.create().show();
    }

    /**
     * sqlから住所一覧取得
     */
    public void getAllAddress(){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.d("LOG: ", " ここはgetAllAddress ");

        try {
            Cursor c = db.query(DatabaseHelper.TABLE_MAPRECORD, new String[]{DatabaseHelper.COLUMN_DATE,
                            DatabaseHelper.COLUMN_ADDRESS, DatabaseHelper.COLUMN_CONTENT},
                    null, null, null, null, null);

            StringBuffer sb = new StringBuffer();

            boolean isEof = c.moveToFirst();
            // データを取得していく
            while(isEof) {
                String date = c.getString(0);
                String address = c.getString(1);
                String content = c.getString(2);

                Log.d("MapsActivity", " ここがLog " + date +" "+ address + "今：" + currentPlace);

                if(currentPlace.equals(address)){
                    Log.d("TODO", "  ここで通知をする");
                    callNotification(date, address, content);
                }
                isEof = c.moveToNext();
            }
            // 忘れずに閉じる
            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    /**
     * 通知をする
     */
    public void callNotification(String date, String address, String content){
//        EditText editText = (EditText)findViewById(R.id.editText);
        Intent bootIntent = new Intent(MapsActivity.this, AlarmReceiver.class);
        bootIntent.putExtra("date", date);
        bootIntent.putExtra("address", address);
        bootIntent.putExtra("content", content);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(MapsActivity.this, 0, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        notificationId++;
    }

}
