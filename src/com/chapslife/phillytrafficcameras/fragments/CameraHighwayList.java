package com.chapslife.phillytrafficcameras.fragments;

import com.chapslife.phillytrafficcameras.activities.CameraListActivity;
import com.chapslife.phillytrafficcameras.activities.FullImageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author kchapman
 *
 */
public class CameraHighwayList extends ListFragment{

    public static String ROAD_ID = "roadid";
    public static String ROAD_NAME = "roadName";
    public CameraHighwayList() {
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, new String[]{ "Interstate 476", "Interstate 676", "Interstate 76", "Interstate 95", "Other Roads"}));
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent gridActivity = new Intent(getActivity(), CameraListActivity.class);
        gridActivity.putExtra(ROAD_NAME, (String)l.getItemAtPosition(position));
        switch(position){
        case 0:
            gridActivity.putExtra(ROAD_ID, "i476");
            getActivity().startActivity(gridActivity);
            break;
        case 1:
            gridActivity.putExtra(ROAD_ID, "i676");
            getActivity().startActivity(gridActivity);
            break;
        case 3:
            gridActivity.putExtra(ROAD_ID, "i76");
            getActivity().startActivity(gridActivity);
            break;
        case 4:
            gridActivity.putExtra(ROAD_ID, "i95");
            getActivity().startActivity(gridActivity);
            break;
        case 5:
            gridActivity.putExtra(ROAD_ID, "nonhighway");
            getActivity().startActivity(gridActivity);
            break;
        }
        
    }
}
