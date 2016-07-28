package com.inffinix.plugins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by eduardo on 13/07/16.
 */
public class LocationDAOSQLLite implements LocationDAO{
    public static final String TABLE_NAME = "location";
    public static final String CN_ID = "_id";
    public static final String CN_LATITUDE = "latitude";
    public static final String CN_LONGITUDE = "longitude";
    public static final String CN_DATE = "date";
    public static final String CN_TYPE = "type";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + CN_ID + " integer primary key autoincrement, "
            + CN_LATITUDE + " real not null, "
            + CN_LONGITUDE + " real not null, "
            + CN_DATE + " date not null, "
            + CN_TYPE + " integer not null );";

    private DbHelper helper;
    private SQLiteDatabase db;
    private List<Location> list;
    private Cursor cursor;

    public LocationDAOSQLLite(Context context ) {
        helper = new DbHelper( context );
        db = helper.getWritableDatabase();
    }

    public static String DateToString(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }


    public static Date StringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedCurrentDate;
        try {
            convertedCurrentDate = sdf.parse(date);
        } catch (ParseException ex) {
            convertedCurrentDate = new Date();
        }
        return convertedCurrentDate;
    }

    public ContentValues generationContentValues( Location location ) {
        ContentValues values = new ContentValues();
        values.put( CN_LATITUDE, location.getLatitude() );
        values.put( CN_LONGITUDE, location.getLongitude() );
        values.put( CN_DATE, DateToString(location.getDate()) );
        values.put( CN_TYPE, location.getType() );
        return values;
    }

    public void insert( Location location ){
        db.insert(TABLE_NAME, null, generationContentValues( location ));
    }

    public void delete( int id ) {
        db.delete(TABLE_NAME, CN_ID + "=?", new String[]{Integer.toString( id )});
    }

    public void chargeCursor(){
        String[] rows = new String[]{ CN_ID, CN_LATITUDE, CN_LONGITUDE, CN_DATE, CN_TYPE };
        cursor = db.query( TABLE_NAME, rows, null, null, null, null, null );
    }

    public List<Location> getAll() {
        list = new ArrayList< Location >();
        chargeCursor();
        int id;
        double latitude;
        double longitude;
        Date date;
        int type;
        Location location;
        while( cursor.moveToNext() ) {
            id = cursor.getInt(cursor.getColumnIndex(CN_ID));
            latitude = cursor.getDouble(cursor.getColumnIndex(CN_LATITUDE));
            longitude = cursor.getDouble(cursor.getColumnIndex(CN_LONGITUDE));
            date = StringToDate( cursor.getString(cursor.getColumnIndex(CN_DATE)));
            type = cursor.getInt(cursor.getColumnIndex(CN_TYPE));
            location = new Location( id, latitude, longitude, date, type );
            list.add( location );
        }
        return list;
    }
}
