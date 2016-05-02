package com.inffinix.plugins;

import android.util.Log;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eduardo Jimenez on 13/01/2016.
 */
public class NetworkService extends BackgroundService {
    private static final String TAG = "FILE TRANSFER";
    private static final String CHARSET = "UTF-8";
    private static final String KEY_ARRAY = "files";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_SERVER = "server";
    private static final String KEY_PARAMS = "params";
    private static final String USER_AGENT = "inffinix";

    private HttpFileUploader httpFileUploader;
    private JSONObject params;
    private JSONObject result;
    private JSONArray elementsResponse;
    private String filePath;
    private String server;
    private String fileName;
    private List< JSONObject> JSONelements = new ArrayList<JSONObject>();
    private List<String> response;
    Iterator<JSONObject> iterator;


    @Override
    protected JSONObject doWork() {
        //configuration was initializing on setConfig
        result = null;
        if ( !JSONelements.isEmpty() ){
            try {
                result = new JSONObject();
                elementsResponse = new JSONArray();

                iterator = JSONelements.iterator();
                while( iterator.hasNext() ) {
                    JSONObject element = iterator.next();

                    filePath = element.getString( KEY_FILE_PATH );
                    server = element.getString( KEY_SERVER );
                    fileName = element.getString( KEY_FILE_NAME );
                    Log.v(TAG, KEY_FILE_PATH + " = " + filePath + " ,  " + KEY_SERVER + " = " + server + " ,  " + KEY_FILE_NAME + " = " + fileName);
                    httpFileUploader = new HttpFileUploader( server, CHARSET );
                    httpFileUploader.addHeaderField("User-Agent", USER_AGENT);

                    File sourceFile = new File( filePath );
                    if( sourceFile.exists() ){
                        httpFileUploader.addFilePart( fileName, sourceFile );
                    }

                    if ( element.has( KEY_PARAMS ) ) {
                        params = element.getJSONObject( KEY_PARAMS );
                        for ( int j = 0; j < params.names().length(); j++ ) {
                            Log.v( TAG, "key = " + params.names().getString( j ) + " value = " + params.get( params.names().getString( j ) ) );
                            httpFileUploader.addFormField(params.names().getString(j), params.get(params.names().getString(j)).toString());
                        }
                    }

                    //it processes response
                    response = httpFileUploader.finish();
                    System.out.print("SERVER REPLIED: ");
                    for ( String line : response ) {
                        System.out.println( line );
                    }
                    iterator.remove();
                    elementsResponse.put( element );
                }

                //return to js
                result.put( KEY_ARRAY, elementsResponse );

            } catch ( JSONException e ) {
                e.printStackTrace();
            } catch ( IOException e ) {
              e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected JSONObject getConfig() {
        JSONObject result = new JSONObject();
        Log.d( TAG, "--------------------- getConfig -----------------------" );
        return result;
    }

    @Override
    protected void setConfig( JSONObject element ) {
        Log.d(TAG, "--------------------- setConfig ---------------------");
        if ( element.has( KEY_FILE_PATH ) && element.has( KEY_SERVER ) && element.has( KEY_FILE_NAME ) ){
            JSONelements.add( element );
        }
    }

    @Override
    protected JSONObject initialiseLatestResult() {
        JSONObject result = new JSONObject();
        return result;
    }
}
