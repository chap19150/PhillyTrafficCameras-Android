package com.chapslife.phillytrafficcameras.penndot.camapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chapslife.phillytrafficcameras.interfaces.PenndotInterface;
import com.chapslife.phillytrafficcameras.utils.Constants;
import com.chapslife.phillytrafficcameras.utils.RequestManager;

/**
 * @author kchapman
 * 
 */
public class PenndotApi {

    /** logging tag **/
    private static final String TAG = PenndotApi.class.getSimpleName();
    /** base url for all calls **/
    public static final String PENNDOT_BASE_URL = "http://7677mobile.com/penndot_api/";
    /** endpoint for closest camera **/
    public static final String GET_CAMERA_BY_LATLNG = "getCamerasByLatLng.php?";
    /** endpoint for getting camera by road id **/
    public static final String GET_CAMERA_BY_ROADID = "getCamerasByRoadId.php?";

    /** request **/
    private JsonObjectRequest mPenndotRequest;

    /** callback interface for top artists calls **/
    private PenndotInterface mPenndotInterface;

    /**
     * Default constructor
     */
    public PenndotApi(Context context) {
    }

    /** Success response for top artists call **/
    private Response.Listener<JSONObject> penndotCameraSuccessListener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            mPenndotInterface.onCamerasReceived(response);
        }
    };

    /** error response for top artists call **/
    private Response.ErrorListener penndotCameraErrorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            mPenndotInterface.onCamerasReceivedError(error.getMessage());
        }
    };

    public void getClosestCameras(PenndotInterface _interface, Double lat, Double lng) {
        mPenndotInterface = _interface;
        // params for the get request
        String params = urlEncode(new String[] { Constants.PARAM_LAT,
                String.valueOf(lat),Constants.PARAM_LNG,
                String.valueOf(lng), Constants.PARAM_FORMAT,
                Constants.PARAM_JSON });
        // construct the url
        String url = PENNDOT_BASE_URL + GET_CAMERA_BY_LATLNG + params;
        Log.d(TAG, url);
        mPenndotRequest = new JsonObjectRequest(url, null, penndotCameraSuccessListener,
                penndotCameraErrorListener);
        mPenndotRequest.setShouldCache(true);
        // add to volley queue
        RequestManager.getRequestQueue().add(mPenndotRequest);
    }

    public void getCameraByRoadId(PenndotInterface _interface, String roadId) {
        mPenndotInterface = _interface;
        // params for the get request
        String params = urlEncode(new String[] { Constants.PARAM_ROADID, roadId,
                Constants.PARAM_FORMAT, Constants.PARAM_JSON });
        // construct the url
        String url = PENNDOT_BASE_URL + GET_CAMERA_BY_ROADID + params;

        mPenndotRequest = new JsonObjectRequest(url, null, penndotCameraSuccessListener,
                penndotCameraErrorListener);
        mPenndotRequest.setShouldCache(true);
        // add to volley queue
        RequestManager.getRequestQueue().add(mPenndotRequest);
    }

    /**
     * URL encodes an array of parameters into a query string. Params must have
     * an even number of elements.
     * 
     * @param params
     * @return
     */
    public static String urlEncode(String[] params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Params must have an even number of elements.");
        }

        String result = Constants.EMPTY_STRING;
        try {
            boolean firstTime = true;
            for (int i = 0; i < params.length; i += 2) {
                if (params[i + 1] != null) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        result += Constants.AMPERSAND;
                    }
                    result += URLEncoder.encode(params[i], Constants.UTF_CHARSET)
                            + Constants.EQUALS
                            + URLEncoder.encode(params[i + 1], Constants.UTF_CHARSET);
                }
            }
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        return result;
    }
}
