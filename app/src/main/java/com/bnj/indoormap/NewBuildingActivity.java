package com.bnj.indoormap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bnj.indoormap.NewBuildingFragment.OnFragmentInteractionListener;

public class NewBuildingActivity extends FragmentActivity implements
        OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_building);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO Auto-generated method stub

    }

}
