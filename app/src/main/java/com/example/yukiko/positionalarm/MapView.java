package com.example.yukiko.positionalarm;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Created by yukiko on 16/11/08.
 */
public class MapView extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOLOADER_ID =0;
    private DatabaseHelper mDbHeloer;
    private ListAdapter mAdapter;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.view);

        Button btnView = (Button)this.findViewById(R.id.btnRet);
        btnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new ListAdapter(this, null, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(CURSOLOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Cursorを取得
        return new CursorLoader(this, MapRecordContentProvider.CONTENT_URI, null, null, null, "_id DESC");
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }



}
