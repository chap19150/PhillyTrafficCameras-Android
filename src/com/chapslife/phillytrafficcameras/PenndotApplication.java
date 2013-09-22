package com.chapslife.phillytrafficcameras;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;

import com.chapslife.phillytrafficcameras.utils.ImageCacheManager;
import com.chapslife.phillytrafficcameras.utils.ImageCacheManager.CacheType;
import com.chapslife.phillytrafficcameras.utils.RequestManager;

public class PenndotApplication extends Application {

    /** size of the disk image cache **/
    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;
    /** compression format used **/
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.JPEG;
    /** quality of the images **/
    private static int DISK_IMAGECACHE_QUALITY = 40; // PNG is lossless so
                                                     // quality is ignored but
                                                     // must be provided
    public static float sAnimatorScale = 1;
    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.init(this);
        createImageCache();
    }
    
    /**
     * Create the image cache. Uses Memory Cache by default. Change to Disk for
     * a Disk based LRU implementation.
     */
    private void createImageCache() {
        ImageCacheManager.getInstance().init(this, this.getPackageCodePath(), getDefaultLruCacheSize(),
                DISK_IMAGECACHE_COMPRESS_FORMAT, DISK_IMAGECACHE_QUALITY, CacheType.MEMORY);
    }

    /**
     * Gets the default LRU cache size
     * @return
     */
    public int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return 1;
    }
}
