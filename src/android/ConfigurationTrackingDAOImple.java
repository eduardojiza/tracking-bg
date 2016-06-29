package com.inffinix.plugins;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by eduardo on 29/06/16.
 */

public class ConfigurationTrackingDAOImple implements ConfigurationTrackingDAO{
    private static final String TAG = "FILE LOCATION";
    private static final String TAG_PARSE_FILE = "En obtencion de datos de getconfig location";
    private static final String TAG_OPEN_FILE = "En abrir archivo de getLocation";
    private static String PATH_FILE = "persistenceLocation";
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Context ctx;

    public ConfigurationTrackingDAOImple(Context ctx) {
        this.ctx = ctx;
    }

    public int insert( ConfigurationTracking configurationTracking ){
        int regreso = 0;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PATH_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("login", configurationTracking.getLogin());
        editor.putString("password", configurationTracking.getPassword());
        editor.putString("server", configurationTracking.getServerLocation());
        editor.commit();
        return regreso;
    }

    public ConfigurationTracking getConfig() {
        ConfigurationTracking configuration = new ConfigurationTracking();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(PATH_FILE, Context.MODE_PRIVATE);
        configuration.setLogin( sharedPreferences.getString( "login", null) );
        configuration.setPassword(sharedPreferences.getString("password", null));
        configuration.setServerLocation( sharedPreferences.getString("server", null) );
        return configuration;
    }


}