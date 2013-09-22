package com.chapslife.phillytrafficcameras.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chapslife.phillytrafficcameras.R;
import com.chapslife.phillytrafficcameras.pojo.Cameras;
import com.chapslife.phillytrafficcameras.pojo.Cameras.Camera;
import com.chapslife.phillytrafficcameras.utils.ImageCacheManager;
import com.chapslife.phillytrafficcameras.viewholders.GridViewHolder;

/**
 * @author kchapman
 *
 */
public class GridViewAdapter extends BaseAdapter {

    private Cameras mCameras;
    private Context mContext;
    private LayoutInflater mInflater;

    /**
     * Constructor
     *
     * @param context
     *            The application context
     * @param entries
     *            - The entries in the folder
     */
    public GridViewAdapter(Context context, Cameras cameras) {
        mCameras = cameras;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        if(mCameras != null && mCameras.cameras != null){
            return mCameras.cameras.size();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Cameras getItem(int position) {
        // do nothing
        return mCameras.cameras.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // do nothing
        return 0;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        GridViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_grid_image, parent, false);
            viewHolder = new GridViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (GridViewHolder) view.getTag();
        }
        Camera camera = mCameras.cameras.get(position).camera;
        if (mCameras != null) {
            viewHolder.getThumb().setDefaultImageResId(R.drawable.default_camera);
            viewHolder.getThumb().setErrorImageResId(R.drawable.default_camera);
            
            if(camera != null){
                viewHolder.getThumb().setImageUrl(camera.url,
                        ImageCacheManager.getInstance().getImageLoader());
            }

        }
        return view;
    }

}
