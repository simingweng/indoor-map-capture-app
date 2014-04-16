/**
 *
 */
package com.bnj.indoormap;

import android.app.Application;

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
        ImageLoader.getInstance().init(
                ImageLoaderConfiguration.createDefault(this));
    }

}
