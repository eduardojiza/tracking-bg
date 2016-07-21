package com.inffinix.plugins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eduardo_Jimenez on 14/07/2016.
 */
public class FileToSendDAOSQLite implements FileToSendDAO{
    public static final String TABLE_NAME = "fileToSend";
    public static final String CN_ID = "_id";
    public static final String CN_FILE_PATH = "filePath";
    public static final String CN_SERVER = "server";
    public static final String CN_FILE_NAME = "fileName";
    public static final String CN_PASSWORD = "password";
    public static final String CN_LOGIN = "login";
    public static final String CN_GROUP = "groupAccount";
    public static final String CN_ACCOUNT = "account";
    public static final String CN_DESCRIPTION = "description";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( "
            + CN_ID + " integer primary key autoincrement, "
            + CN_FILE_PATH + " text not null, "
            + CN_SERVER + " text not null, "
            + CN_FILE_NAME + " text not null, "
            + CN_PASSWORD + " text not null, "
            + CN_LOGIN + " text not null, "
            + CN_GROUP + " text not null, "
            + CN_ACCOUNT + " text not null, "
            + CN_DESCRIPTION + " text null );";

    private DbHelper helper;
    private SQLiteDatabase db;
    private List<FileToSend> list;
    private Cursor cursor;

    public FileToSendDAOSQLite(Context context ) {
        helper = new DbHelper( context );
        db = helper.getWritableDatabase();
    }

    public ContentValues generationContentValues( FileToSend fileToSend ) {
        ContentValues values = new ContentValues();
        values.put( CN_FILE_PATH, fileToSend.getFilePath() );
        values.put( CN_SERVER, fileToSend.getServer() );
        values.put( CN_FILE_NAME, fileToSend.getFileName() );
        values.put( CN_PASSWORD, fileToSend.getPassword() );
        values.put( CN_LOGIN, fileToSend.getLogin() );
        values.put( CN_GROUP, fileToSend.getGroup() );
        values.put( CN_ACCOUNT, fileToSend.getAccount() );
        values.put( CN_DESCRIPTION, fileToSend.getDescription() );
        return values;
    }

    public void insert( FileToSend fileToSend ){
        db.insert(TABLE_NAME, null, generationContentValues( fileToSend ));
    }

    public void delete( int id ) {
        db.delete(TABLE_NAME, CN_ID + "=?", new String[]{Integer.toString( id )});
    }

    public void chargeCursor(){
        String[] rows = new String[]{ CN_ID, CN_FILE_PATH, CN_SERVER, CN_FILE_NAME, CN_PASSWORD, CN_LOGIN, CN_GROUP, CN_ACCOUNT, CN_DESCRIPTION };
        cursor = db.query( TABLE_NAME, rows, null, null, null, null, null );
    }

    public List<FileToSend> getAll() {
        list = new ArrayList< FileToSend >();
        chargeCursor();
        int id;
        String filePath;
        String server;
        String fileName;
        String password;
        String login;
        String group;
        String account;
        String description;
        FileToSend fileToSend;
        while( cursor.moveToNext() ) {
            id = cursor.getInt(cursor.getColumnIndex(CN_ID));
            filePath = cursor.getString(cursor.getColumnIndex(CN_FILE_PATH));
            server = cursor.getString(cursor.getColumnIndex(CN_SERVER));
            fileName = cursor.getString(cursor.getColumnIndex(CN_FILE_NAME));
            password = cursor.getString(cursor.getColumnIndex(CN_PASSWORD));
            login = cursor.getString(cursor.getColumnIndex(CN_LOGIN));
            group = cursor.getString(cursor.getColumnIndex(CN_GROUP));
            account = cursor.getString(cursor.getColumnIndex(CN_ACCOUNT));
            description = cursor.getString(cursor.getColumnIndex(CN_DESCRIPTION));


            //(int id, double latitude, double longitude, Date date, int type)
            fileToSend = new FileToSend(id, description, account, group, login, password, server, fileName, filePath);
            list.add( fileToSend );
        }
        return list;
    }
}
