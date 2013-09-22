package com.chapslife.phillytrafficcameras.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.chapslife.phillytrafficcameras.R;
import com.chapslife.phillytrafficcameras.adapters.GridViewAdapter;
import com.chapslife.phillytrafficcameras.fragments.CameraHighwayList;
import com.chapslife.phillytrafficcameras.interfaces.PenndotInterface;
import com.chapslife.phillytrafficcameras.penndot.camapi.PenndotApi;
import com.chapslife.phillytrafficcameras.pojo.Cameras;
import com.google.gson.Gson;

/**
 * @author kchapman
 *
 */
public class CameraListActivity extends Activity implements OnItemClickListener, PenndotInterface{

    private static final String PACKAGE = "com.chapslife.phillytrafficcameras";
    private PenndotApi mPenndotApi;
    private GridView mGridView;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_closest_camera);
        mPenndotApi = new PenndotApi(getApplicationContext());
        mGridView = (GridView) findViewById(R.id.wallpaper_grid);
        mGridView.setOnItemClickListener(this);
        Bundle bundle = getIntent().getExtras();
        String roadId = bundle.getString(CameraHighwayList.ROAD_ID);
        String roadName = bundle.getString(CameraHighwayList.ROAD_NAME);
        getActionBar().setTitle(roadName);
        getActionBar().setDisplayShowTitleEnabled(true);
        mPenndotApi.getCameraByRoadId(this, roadId);
    }

    /* (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        Cameras cameras = (Cameras) mGridView.getItemAtPosition(position);
        // Interesting data to pass across are the thumbnail size/location, the
        // resourceId of the source bitmap, the picture description, and the
        // orientation (to avoid returning back to an obsolete configuration if
        // the device rotates again in the meantime)
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        Intent subActivity = new Intent(this, FullImageActivity.class);
        int orientation = getResources().getConfiguration().orientation;
        subActivity.putExtra(PACKAGE + ".orientation", orientation)
                .putExtra(PACKAGE + ".url", cameras.camera.url)
                .putExtra(PACKAGE + ".left", screenLocation[0])
                .putExtra(PACKAGE + ".top", screenLocation[1])
                .putExtra(PACKAGE + ".width", view.getWidth())
                .putExtra(PACKAGE + ".height", view.getHeight());
        startActivity(subActivity);

        // Override transitions: we don't want the normal window animation in
        // addition
        // to our custom one
        overridePendingTransition(0, 0);
    }

    /* (non-Javadoc)
     * @see com.chapslife.phillytrafficcameras.interfaces.PenndotInterface#onCamerasReceived(org.json.JSONObject)
     */
    @Override
    public void onCamerasReceived(JSONObject response) {
        Cameras cameras = gson.fromJson(response.toString(), Cameras.class);
        if (cameras != null && cameras.cameras.size() > 0) {
            mGridView.setAdapter(new GridViewAdapter(this, cameras));
        }
    }

    /* (non-Javadoc)
     * @see com.chapslife.phillytrafficcameras.interfaces.PenndotInterface#onCamerasReceivedError(java.lang.String)
     */
    @Override
    public void onCamerasReceivedError(String error) {
        // do nothing
        
    }
}
