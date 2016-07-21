package com.inffinix.plugins;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Eduardo Jimenez on 13/01/2016.
 */
public class NetworkService extends BackgroundService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    //Parse JSON File
    private static final String TAG = "FILE TRANSFER";
    private static final String CHARSET = "UTF-8";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_SERVER = "server";
    private static final String KEY_PARAMS = "params";
    private static final String USER_AGENT = "inffinix";
    private static final String KEY_ARRAY = "files";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_GROUP = "group";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_DESCRIPTION = "description";

    //Parse JSON Location
    private static final String KEY_SERVER_LOCATION = "serverLocation";
    private static final String KEY_PASSWORD_LOCATION = "password";
    private static final String KEY_USER_LOCATION = "login";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final int TYPE_LOCATION_OK = 0;
    private static final int TYPE_LOCATION_ERROR = 1;

    private HttpFileUploader httpFileUploader;
    private FileToSendDAO fileToSendDAO;
    private List< FileToSend > files;
    private List< FileToSend > filesSended;
    private JSONArray filesTemp;
    private JSONObject result = null;
    private List< String > response = null;
    private LocationDAO locationDAO;
    private List<com.inffinix.plugins.Location> locations;
    private boolean isWorking = false;

    //Location
    private String uriLocation = null;
    private String passlocation = null;
    private String userLocation = null;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest = null;
    private static int UPDATE_INTERVAL = 60*1000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10;
    private ConfigurationTracking configurationTracking = null;
    private ConfigurationTrackingDAO configurationTrackingDAO = null;

    @Override
    protected JSONObject doWork() {
        //it sends the information of Files to send and location saved
        if( InternetConnection.isInternetWorking() && !isWorking){
            SendSaveInfo sendInfo = new SendSaveInfo();
            sendInfo.execute();
        }

        //it generates the response to view a truncate the cola
        try {
            if( !filesSended.isEmpty() ){
                filesTemp = arrayFileToSendtoArrayJSON( filesSended );
                filesSended = new ArrayList<FileToSend>();
            } else {
                filesTemp = new JSONArray();
            }
            result = buildResponse( filesTemp );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List< FileToSend > createTemp(List< FileToSend > files){
        List< FileToSend > temp = new ArrayList<FileToSend>(files.size());

        return temp;
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
        try {
            if ( element.has( KEY_FILE_PATH ) && element.has( KEY_SERVER ) && element.has( KEY_FILE_NAME ) ){
                fileToSendDAO.insert( JSONObjectToFileToSend( element ) );
            } else if ( element.has( KEY_SERVER_LOCATION ) && element.has( KEY_USER_LOCATION ) && element.has( KEY_PASSWORD_LOCATION ) ) {
                configurationTracking = new ConfigurationTracking( element.getString( KEY_SERVER_LOCATION ), element.getString( KEY_PASSWORD_LOCATION), element.getString( KEY_USER_LOCATION ), 0.0, 0.0  );
                configurationTrackingDAO = new ConfigurationTrackingDAOImple( this );
                configurationTrackingDAO.insert( configurationTracking  );
                setDataLocation();
            }
        } catch (JSONException e) {
            Log.d( TAG, "--------------------- Estructura invalida para configuracion -----------------------" );
        }

    }

    @Override
    protected JSONObject initialiseLatestResult() {
        Log.d(TAG, "--------------------- initialiseLatestResult ---------------------");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        result = null;
        fileToSendDAO = new FileToSendDAOSQLite( this );
        locationDAO = new LocationDAOSQLLite( this );
        filesSended = new ArrayList<FileToSend>();
        filesTemp = new JSONArray();
        startLocation();
        return result;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "--------------------- onConnected ---------------------");
        if (!mRequestingLocationUpdates) {
            configurationTrackingDAO = new ConfigurationTrackingDAOImple(this);
            configurationTracking = configurationTrackingDAO.getConfig();
            setDataLocation();
            startLocationUpdates();
            mRequestingLocationUpdates = true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ) {
        Log.d(TAG, "--------------------- onConnectionFailed ---------------------");
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "--------------------- startLocationUpdates ---------------------");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        Log.d(TAG, "--------------------- stopLocationUpdates ---------------------");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
        if ( resultCode != ConnectionResult.SUCCESS ) {
            if ( GooglePlayServicesUtil.isUserRecoverableError( resultCode ) ) {
            } else {
                Toast.makeText( getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( UPDATE_INTERVAL );
        mLocationRequest.setFastestInterval( FATEST_INTERVAL );
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY );
        mLocationRequest.setSmallestDisplacement( DISPLACEMENT );
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
    }

    private void displayLocation( com.inffinix.plugins.Location location, String postfix ) {
        Log.d( TAG, "longitude: " + location.getLongitude() + postfix );
        Log.d( TAG, "latidude: " + location.getLatitude() + postfix );
    }

    private void sendLocation(com.inffinix.plugins.Location location) throws IOException {//throws IOException{
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uriLocation);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
        nameValuePair.add( new BasicNameValuePair( KEY_PASSWORD_LOCATION, passlocation));
        nameValuePair.add( new BasicNameValuePair( KEY_USER_LOCATION, userLocation));
        nameValuePair.add( new BasicNameValuePair( KEY_LATITUDE, Double.toString( location.getLatitude() ) ) );
        nameValuePair.add( new BasicNameValuePair( KEY_LONGITUDE, Double.toString( location.getLongitude() ) ) );
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        httpClient.execute(httpPost);
    }

    private void setDataLocation(){
        if( configurationTracking.getServerLocation() != null && configurationTracking.getPassword() != null && configurationTracking.getLogin() != null ) {
            uriLocation = configurationTracking.getServerLocation();
            userLocation = configurationTracking.getLogin();
            passlocation = configurationTracking.getPassword();
        }
    }

    private List< String > sendFile( FileToSend fileToSend ) throws IOException {
        httpFileUploader = new HttpFileUploader( fileToSend.getServer(), CHARSET );
        httpFileUploader.addHeaderField("User-Agent", USER_AGENT);

        httpFileUploader.addFormField(KEY_PASSWORD, fileToSend.getPassword());
        httpFileUploader.addFormField(KEY_LOGIN, fileToSend.getLogin());
        httpFileUploader.addFormField(KEY_GROUP, fileToSend.getGroup());
        httpFileUploader.addFormField(KEY_ACCOUNT, fileToSend.getAccount());
        httpFileUploader.addFormField(KEY_DESCRIPTION, fileToSend.getDescription());

        File sourceFile = new File( fileToSend.getFilePath() );
        if( sourceFile.exists() ){
            httpFileUploader.addFilePart(fileToSend.getFileName(), sourceFile);
        }

        return httpFileUploader.finish();
    }

    private void startLocation(){
        // it checks availability of play services
        if ( mGoogleApiClient == null && checkPlayServices() ) {
            buildGoogleApiClient();
            createLocationRequest();
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
        }
    }

    private boolean thereAreConfigurationsLocation(){
        return uriLocation != null && userLocation != null && passlocation != null;
    }

    @Override
    public void onLocationChanged( Location location) {
        Log.d(TAG, "--------------------- onLocationChanged ---------------------");
        if ( thereAreConfigurationsLocation() ) {
            SendLocationAsync send = new SendLocationAsync();
            send.execute( location );
        }
    }

    private class SendLocationAsync extends AsyncTask<Location, Void, Void>{
        @Override
        protected Void doInBackground(Location... params) {
            com.inffinix.plugins.Location locationSend = new com.inffinix.plugins.Location(0, params[0].getLatitude(), params[0].getLongitude(), new Date(), TYPE_LOCATION_OK);
            try {
                sendLocation( locationSend );
                displayLocation(locationSend, "--------------");
            } catch (IOException e) {
                //if it cant send it will save in bd
                locationSend.setType( TYPE_LOCATION_ERROR );
                locationDAO.insert( locationSend );
                displayLocation(locationSend, "***************");
                e.printStackTrace();
            }
            return null;
        }
    }

    private JSONObject fileToSendToJSONObject( FileToSend fileToSend ) throws JSONException {
        JSONObject objectTemp = new JSONObject();
        objectTemp.put(KEY_SERVER, fileToSend.getServer());
        objectTemp.put(KEY_FILE_NAME, fileToSend.getFileName());
        objectTemp.put(KEY_FILE_PATH, fileToSend.getFilePath());
        return objectTemp;
    }

    private FileToSend JSONObjectToFileToSend( JSONObject element ) throws JSONException {
        JSONObject parameters = element.getJSONObject( KEY_PARAMS );
        FileToSend temp = new FileToSend( 0, parameters.getString(KEY_DESCRIPTION) ,
                parameters.getString(KEY_ACCOUNT),
                parameters.getString(KEY_GROUP),
                parameters.getString(KEY_LOGIN),
                parameters.getString(KEY_PASSWORD),
                element.getString(KEY_SERVER),
                element.getString(KEY_FILE_NAME),
                element.getString(KEY_FILE_PATH) );
        return temp;
    }

    private JSONObject buildResponse( JSONArray array ) throws JSONException {
        JSONObject object = new JSONObject();
        object.put( KEY_ARRAY, array );
        return object;
    }

    private JSONArray arrayFileToSendtoArrayJSON( List< FileToSend > array ) throws JSONException {
        JSONArray temp = new JSONArray();
        for( FileToSend file : array ){
            temp.put( fileToSendToJSONObject( file ) );
        }
        return  temp;
    }

    private class SendSaveInfo extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            //configuration was initializing on setConfig
            //process Files
            isWorking = true;
            files = fileToSendDAO.getAll();
            if( !files.isEmpty() ) {
                for( FileToSend fileToSend : files ) {
                    try {
                        response = sendFile( fileToSend );
                        for ( String line : response ) {
                            Log.d( TAG, "SERVER REPLIED " + line );
                        }

                        fileToSendDAO.delete(fileToSend.getId());
                        filesSended.add( fileToSend );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //process location
            if ( thereAreConfigurationsLocation() ){
                locations = locationDAO.getAll();
                if( !locations.isEmpty() ) {
                    for( com.inffinix.plugins.Location loc : locations ) {
                        try {
                            sendLocation( loc );
                            locationDAO.delete( loc.getId() );
                            displayLocation(loc, "..............");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            isWorking = false;
            return null;
        }
    }
}
