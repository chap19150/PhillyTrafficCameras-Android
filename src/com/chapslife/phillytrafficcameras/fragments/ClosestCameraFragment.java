package com.chapslife.phillytrafficcameras.fragments;

import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.chapslife.phillytrafficcameras.R;
import com.chapslife.phillytrafficcameras.activities.FullImageActivity;
import com.chapslife.phillytrafficcameras.adapters.GridViewAdapter;
import com.chapslife.phillytrafficcameras.interfaces.PenndotInterface;
import com.chapslife.phillytrafficcameras.penndot.camapi.PenndotApi;
import com.chapslife.phillytrafficcameras.pojo.Cameras;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;

/**
 * @author kchapman
 * 
 */
public class ClosestCameraFragment extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, Runnable, PenndotInterface,
        OnItemClickListener {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    /** logging tag **/
    private static final String TAG = ClosestCameraFragment.class.getSimpleName();
    private static final String PACKAGE = "com.chapslife.phillytrafficcameras";
    /**
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;
    private PenndotApi mPenndotApi;
    private Gson gson = new Gson();
    private GridView mGridView;

    public ClosestCameraFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_closest_camera, container, false);

        mPenndotApi = new PenndotApi(getActivity().getApplicationContext());
        // Create a new location client, using the enclosing class to handle
        // callbacks.
        mLocationClient = new LocationClient(getActivity(), this, this);
        mGridView = (GridView) rootView.findViewById(R.id.wallpaper_grid);
        // mPenndotApi.getClosestCameras(this, Double.valueOf(39.952473),
        // Double.valueOf(-75.164106));
        mGridView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.android.gms.common.GooglePlayServicesClient.
     * OnConnectionFailedListener
     * #onConnectionFailed(com.google.android.gms.common.ConnectionResult)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, String.valueOf(result.getErrorCode()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
     * #onConnected(android.os.Bundle)
     */
    @Override
    public void onConnected(Bundle arg0) {
        run();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
     * #onDisconnected()
     */
    @Override
    public void onDisconnected() {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Location loc = mLocationClient.getLastLocation();

        if (loc == null) {
          mPenndotApi.getClosestCameras(this, Double.valueOf(39.952473),Double.valueOf(-75.164106));
        } else {
            mPenndotApi.getClosestCameras(this, loc.getLatitude(), loc.getLongitude());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chapslife.phillytrafficcameras.interfaces.PenndotInterface#
     * onCamerasReceived(org.json.JSONObject)
     */
    @Override
    public void onCamerasReceived(JSONObject response) {
        Log.d(TAG, response.toString());
        Cameras cameras = gson.fromJson(response.toString(), Cameras.class);
        if (cameras != null && cameras.cameras.size() > 0) {
            Log.d(TAG, "Cameras count " + cameras.cameras.size());
            Log.d(TAG, "CAMERA URL " + cameras.cameras.get(0).camera.url);
            mGridView.setAdapter(new GridViewAdapter(getActivity(), cameras));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chapslife.phillytrafficcameras.interfaces.PenndotInterface#
     * onCamerasReceivedError(java.lang.String)
     */
    @Override
    public void onCamerasReceivedError(String error) {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
     * .AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Cameras cameras = (Cameras) mGridView.getItemAtPosition(position);
        // Interesting data to pass across are the thumbnail size/location, the
        // resourceId of the source bitmap, the picture description, and the
        // orientation (to avoid returning back to an obsolete configuration if
        // the device rotates again in the meantime)
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        Intent subActivity = new Intent(getActivity(), FullImageActivity.class);
        int orientation = getResources().getConfiguration().orientation;
        subActivity.putExtra(PACKAGE + ".orientation", orientation)
                .putExtra(PACKAGE + ".url", cameras.camera.url)
                .putExtra(PACKAGE + ".left", screenLocation[0])
                .putExtra(PACKAGE + ".top", screenLocation[1])
                .putExtra(PACKAGE + ".width", view.getWidth())
                .putExtra(PACKAGE + ".height", view.getHeight());
        getActivity().startActivity(subActivity);

        // Override transitions: we don't want the normal window animation in
        // addition
        // to our custom one
        getActivity().overridePendingTransition(0, 0);
    }
}
