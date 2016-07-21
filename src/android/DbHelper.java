package com.inffinix.plugins;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;
import android.util.Log;

/**
 * Created by eduardo on 13/07/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "location.sqlite";
    private static final int DB_SCHEME_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( LocationDAOSQLLite.CREATE_TABLE );
        db.execSQL( FileToSendDAOSQLite.CREATE_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
