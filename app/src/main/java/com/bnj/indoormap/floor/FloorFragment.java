package com.bnj.indoormap.floor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bnj.indoormap.R;
import com.bnj.indoortms.api.client.model.Building;
import com.bnj.indoortms.api.client.model.Floor;
import com.bnj.indoortms.api.client.request.GetBuildingByIdRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.bnj.indoormap.floor
 * .FloorFragment.OnFloorSelectionListener}
 * interface.
 */
public class FloorFragment extends ListFragment {

    private static final String TAG = FloorFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUILDING_ID = "buildingId";

    // TODO: Rename and change types of parameters
    private String buildingId;
    private SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);
    private FloorsArrayAdapter adapter;
    private RequestListener<Building> floorsRequestListener = new RequestListener<Building>() {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, "failed to retrieve the floor list for building " + buildingId);
        }

        @Override
        public void onRequestSuccess(Building building) {
            if (adapter != null) {
                adapter.clear();
                adapter.addAll(building.getFloors());
            }
        }
    };
    private OnFloorSelectionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FloorFragment() {
    }

    // TODO: Rename and change types of parameters
    public static FloorFragment newInstance(String buildingId) {
        FloorFragment fragment = new FloorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BUILDING_ID, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spiceManager.start(getActivity());
        if (getArguments() != null) {
            buildingId = getArguments().getString(ARG_BUILDING_ID);
        }
        adapter = new FloorsArrayAdapter(getActivity(), R.layout.floor_list_item,
                R.id.textViewName, new ArrayList<Floor>());
        setListAdapter(adapter);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GetBuildingByIdRequest request = new GetBuildingByIdRequest(buildingId);
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheKey(),
                DurationInMillis.ONE_HOUR,
                floorsRequestListener);
        setEmptyText(getString(R.string.floor_list_empty_text));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.floor_list_option, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFloorSelectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_floor:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        spiceManager.shouldStop();
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFloorSelected(adapter.getItem(position).get_id());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFloorSelectionListener {
        // TODO: Update argument type and name
        public void onFloorSelected(String id);
    }

    private class FloorsArrayAdapter extends ArrayAdapter<Floor> {

        public FloorsArrayAdapter(Context context, int resource, int textViewResourceId,
                                  List<Floor> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            ImageView image = (ImageView) v.findViewById(R.id.imageView);
            TextView level = (TextView) v.findViewById(R.id.textViewLevel);
            String url = "http://192.168.1.182/%s/%s/%s";
            Floor floor = adapter.getItem(position);
            level.setText("Level " + floor.getLevel());
            ImageLoader.getInstance().displayImage(String.format(url, buildingId, floor.get_id(),
                    floor.getImage()), image);
            return v;
        }
    }

}
