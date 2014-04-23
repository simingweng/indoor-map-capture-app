package com.bnj.indoormap.floor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.bnj.indoormap.R;
import com.bnj.indoormap.utils.Constants;

public class NewFloorActivity extends FragmentActivity {

    private static final String NEW_FLOOR_FRAGMENT_TAG = "new_floor_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_floor);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(NEW_FLOOR_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = NewFloorFragment.newInstance(getIntent().getStringExtra(Constants
                    .CreateNewFloor.BUILDING_ID_EXTRA_KEY), getIntent().getDoubleArrayExtra
                    (Constants
                    .GeoReference.BUILDING_LOCATION_EXTRA_KEY));
        }
        fm.beginTransaction().replace(R.id.frame_container, fragment,
                NEW_FLOOR_FRAGMENT_TAG).commit();
    }

}
