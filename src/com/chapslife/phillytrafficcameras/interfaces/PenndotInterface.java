package com.chapslife.phillytrafficcameras.interfaces;

import org.json.JSONObject;

/**
 * @author kchapman
 *
 */
public interface PenndotInterface {
    
    /**
    * Callback for a successful call when getting cameras
    * @param response
    */
   void onCamerasReceived(JSONObject response);
   
   /**
    * Callback for a failed call getting cameras
    * @param error
    */
   void onCamerasReceivedError(String error);
}
