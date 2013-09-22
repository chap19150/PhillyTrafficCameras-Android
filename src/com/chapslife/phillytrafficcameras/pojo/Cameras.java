package com.chapslife.phillytrafficcameras.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kchapman
 *
 */
public class Cameras {
    
    public ArrayList<Cameras> cameras;
    
    public Camera camera;
    
    public class Camera {
        
        public String id;
        public String lng;
        public String lat;
        public String name;
        public String road;
        public String road_id;
        public String url;
        public String distance;
    }
}
