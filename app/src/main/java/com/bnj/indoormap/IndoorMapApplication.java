/**
 *
 */
package com.bnj.indoormap;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author simingweng
 */
public class IndoorMapApplication extends Application {

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .considerExifParams(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs().defaultDisplayImageOptions(options).build();
        ImageLoader.getInstance().init(config
        );
    }

}
