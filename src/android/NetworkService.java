package com.inffinix.plugins;

import android.location.Location;
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
import org.apache.http.client.ClientProtocolException;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Eduardo Jimenez on 13/01/2016.
 */
public class NetworkService extends BackgroundService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "FILE TRANSFER";
    private static final String CHARSET = "UTF-8";
    private static final String KEY_ARRAY = "files";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_SERVER = "server";
    private static final String KEY_PARAMS = "params";
    private static final String USER_AGENT = "inffinix";
    private static final String KEY_REPPLY = "replied";
    private static final String KEY_REMOVE = "remove";
    private static final String KEY_RESPONSE_OK = "response";

    private HttpFileUploader httpFileUploader;
    private JSONObject params;
    private JSONObject result;
    private JSONArray elementsResponse;
    private String filePath;
    private String server;
    private String fileName;
    private List< JSONObject > JSONelements = new ArrayList<JSONObject>();
    private List< String > response;
    Iterator< JSONObject > iterator;

    //Location
    private String uriLocation = null;
    private String passlocation = null;
    private String userLocation = null;
    private static final String KEY_SERVER_LOCATION = "serverLocation";
    private static final String KEY_PASSWORD_LOCATION = "password";
    private static final String KEY_USER_LOCATION = "login";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private Location mLastLocation = null;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest = null;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private ConfigurationTracking configurationTracking = null;
    private ConfigurationTrackingDAO configurationTrackingDAO = null;

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

                    if( element.has( KEY_REMOVE ) ) {
                        if( element.has( KEY_RESPONSE_OK ) ) {
                            if ( element.getBoolean( KEY_REMOVE ) && response.get( 0 ).equals( element.getString( KEY_RESPONSE_OK ) ) ) {
                                sourceFile.delete();
                            }
                        } else {
                            if ( element.getBoolean( KEY_REMOVE ) ) {
                                sourceFile.delete();
                            }
                        }
                        System.out.print("delete file");
                    }

                    element.put( KEY_REPPLY, response );
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
        } else if ( element.has( KEY_SERVER_LOCATION ) && element.has( KEY_USER_LOCATION ) && element.has( KEY_PASSWORD_LOCATION ) ) {
            try {
                uriLocation = element.getString( KEY_SERVER_LOCATION );
                userLocation = element.getString( KEY_USER_LOCATION );
                passlocation = element.getString( KEY_PASSWORD_LOCATION );
                configurationTracking = new ConfigurationTracking( uriLocation, passlocation, userLocation, 0.0, 0.0  );
                configurationTrackingDAO = new ConfigurationTrackingDAOImple(this);
                configurationTrackingDAO.insert( configurationTracking  );
            } catch (JSONException e) {
                Log.d( TAG, "--------------------- Estructura invalida para configuracion de envio de geolocalizacion -----------------------" );
            }
        }
    }

    @Override
    protected JSONObject initialiseLatestResult() {
        JSONObject result = new JSONObject();
        Log.d(TAG, "--------------------- initialiseLatestResult ---------------------");
        // First we need to check availability of play services
        if ( mGoogleApiClient == null && checkPlayServices() ) {
            buildGoogleApiClient();
        }

        if( mGoogleApiClient != null && mGoogleApiClient != null ){
            createLocationRequest();
        }

        if( mGoogleApiClient != null  && !mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.connect();
        }

        return result;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "--------------------- onConnected ---------------------");
        if ( !mRequestingLocationUpdates ) {
            configurationTrackingDAO = new ConfigurationTrackingDAOImple(this);
            configurationTracking = configurationTrackingDAO.getConfig();
            System.out.println( "informacion obtenida" + configurationTracking );
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
    }

    private void displayLocation() {
        Log.d( TAG, "--------------------- longitude: " + mLastLocation.getLongitude() );
        Log.d( TAG, "--------------------- latidude: " + mLastLocation.getLatitude() );
        Log.d(TAG, "--------------------- Thread: " + Thread.currentThread().getId() );
    }

    private void sendLocation() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if ( SDK_INT > 8 ) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uriLocation);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
            nameValuePair.add( new BasicNameValuePair( KEY_PASSWORD_LOCATION, passlocation));
            nameValuePair.add( new BasicNameValuePair( KEY_USER_LOCATION, userLocation));
            nameValuePair.add( new BasicNameValuePair( KEY_LATITUDE, Double.toString( mLastLocation.getLatitude() ) ) );
            nameValuePair.add( new BasicNameValuePair( KEY_LONGITUDE, Double.toString( mLastLocation.getLongitude() ) ) );
            displayLocation();
            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "EN UrlEncodedFormEntity");
                e.printStackTrace();
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", response.toString());
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
        }
    }

    private void setDataLocation(){
        if( configurationTracking.getServerLocation() != null && configurationTracking.getPassword() != null && configurationTracking.getLogin() != null ) {
            uriLocation = configurationTracking.getServerLocation();
            userLocation = configurationTracking.getLogin();
            passlocation = configurationTracking.getPassword();
        }
    }

    @Override
    public void onLocationChanged( Location location) {
        mLastLocation = location;
        if( uriLocation != null && userLocation != null && passlocation != null ) {
            sendLocation();
        }
    }
}
