package com.bnj.indoormap;

/**
 * Created by simingweng on 16/4/14.
 */
public interface Reloadable {

    public void reload(boolean forceNetwork);

    public void unload();

}
