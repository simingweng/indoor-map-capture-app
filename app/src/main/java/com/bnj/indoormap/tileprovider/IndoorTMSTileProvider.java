package com.bnj.indoormap.tileprovider;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wen55527 on 4/23/14.
 */
public class IndoorTMSTileProvider extends UrlTileProvider {

    private static final String TMS_URL = "http://10.66.96.159:8080/%s/%s/%d/%d/%d.png";
    private String buildingId;
    private String floorId;

    public IndoorTMSTileProvider(int width, int height) {
        super(width, height);
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        int reversedY = (1 << zoom) - y - 1;
        try {
            return new URL(String.format(TMS_URL, buildingId, floorId, x, reversedY, zoom));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
