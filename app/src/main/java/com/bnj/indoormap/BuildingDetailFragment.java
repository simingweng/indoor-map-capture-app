package com.bnj.indoormap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bnj.indoormap.floor.FloorFragment;

/**
 * A fragment representing a single Building detail screen. This fragment is
 * either contained in a {@link BuildingListActivity} in two-pane mode (on
 * tablets) or a {@link BuildingDetailActivity} on handsets.
 */
public class BuildingDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_BUILDING_ID = "building_id";
    private String buildingId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuildingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_BUILDING_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            buildingId = getArguments().getString(ARG_BUILDING_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_building_detail,
                container, false);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        if (pager != null) {
            pager.setAdapter(new BuildingDetailPagerAdapter(getChildFragmentManager()));
        }

        return rootView;
    }

    private class BuildingDetailPagerAdapter extends FragmentPagerAdapter {

        public BuildingDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //return the building info fragment page
                    return BuildingInfoFragment.newInstance(buildingId);
                case 1:
                    //return the building floor list fragment page
                    return FloorFragment.newInstance(buildingId);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Details";
                case 1:
                    return "Floors";
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
