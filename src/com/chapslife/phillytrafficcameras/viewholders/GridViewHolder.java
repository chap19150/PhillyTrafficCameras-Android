package com.chapslife.phillytrafficcameras.viewholders;

import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.chapslife.phillytrafficcameras.R;

/**
 * Viewholder for the cameras
 * 
 */
public class GridViewHolder {

    // ui elements
    private View base;
    private NetworkImageView thumb = null;
    
    /**
     * base view for the grid item
     * 
     * @param base
     */
    public GridViewHolder(View base) {
        this.base = base;
    }

    /**
     * get the {@link NetworkImageView} that holds the thumbnail
     * 
     * @return a {@link NetworkImageView}
     */
    public NetworkImageView getThumb() {
        if (thumb == null) {
            thumb = (NetworkImageView) base.findViewById(R.id.camera_imageview);
        }
        return thumb;
    }
}
