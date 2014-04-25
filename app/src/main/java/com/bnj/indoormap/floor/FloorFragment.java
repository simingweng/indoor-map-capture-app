package com.bnj.indoormap.floor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bnj.indoormap.R;
import com.bnj.indoormap.map.MapsActivity;
import com.bnj.indoormap.utils.Constants;
import com.bnj.indoortms.api.client.model.Building;
import com.bnj.indoortms.api.client.model.Floor;
import com.bnj.indoortms.api.client.request.GetBuildingByIdRequest;
import com.bnj.indoortms.api.client.utils.FloorImageUtil;
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
public class FloorFragment extends ListFragment implements AbsListView.MultiChoiceModeListener {

    private static final String TAG = FloorFragment.class.getName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BUILDING_ID = "buildingId";

    // TODO: Rename and change types of parameters
    private String buildingId;
    private double[] buildingLocation;
    private SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);
    private FloorsArrayAdapter adapter;
    private OnFloorSelectionListener mListener;
    private MenuItem editMenuItem;

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
        setEmptyText(getString(R.string.floor_list_empty_text));
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        getListView().setMultiChoiceModeListener(this);
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
                Intent intent = new Intent(getActivity(), NewFloorActivity.class);
                intent.putExtra(Constants.CreateNewFloor.BUILDING_ID_EXTRA_KEY, buildingId);
                intent.putExtra(Constants.GeoReference.BUILDING_LOCATION_EXTRA_KEY,
                        buildingLocation);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
        GetBuildingByIdRequest request = new GetBuildingByIdRequest(buildingId);
        spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                request.getCacheKey() + ".floors", DurationInMillis.ONE_MINUTE,
                new RequestListener<Building>() {

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(TAG, "failed to retrieve the floor list for building " + buildingId);
                    }

                    @Override
                    public void onRequestSuccess(Building building) {
                        buildingLocation = new double[]{building.getLocation().lat,
                                building.getLocation().lng};
                        if (adapter != null) {
                            adapter.clear();
                            adapter.addAll(building.getFloors());
                        }
                    }
                }
        );
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFloorSelected(adapter.getItem(position).get_id());
        }

        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra(Constants.Map.BUILDING_ID_EXTRA_KEY, buildingId);
        intent.putExtra(Constants.Map.FLOOR_ID_EXTRA_KEY, adapter.getItem(position).get_id());
        intent.putExtra(Constants.Map.BUILDING_LOCATION_EXTRA_KEY, buildingLocation);
        startActivity(intent);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        editMenuItem.setVisible(getListView().getCheckedItemCount() == 1);
        final int checkedCount = getListView().getCheckedItemCount();
        mode.setSubtitle(getResources().getQuantityString(
                R.plurals.multiple_selection_subtitle, checkedCount,
                "floor", checkedCount));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.floor_list_contextual_options, menu);
        editMenuItem = menu.findItem(R.id.edit);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteSelectedFloors();
                break;

            case R.id.edit:
                editSelectedFloor(adapter.getItem(getListView().getCheckedItemPosition()));
                break;
        }
        mode.finish();
        return false;
    }

    private void editSelectedFloor(Floor floor) {

    }

    private void deleteSelectedFloors() {

    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        editMenuItem = null;
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
            Floor floor = adapter.getItem(position);
            level.setText("Level " + floor.getLevel());
            ImageLoader.getInstance().displayImage(FloorImageUtil.getInstance().getImageUrl
                    (buildingId, floor.getImage()), image);
            return v;
        }
    }

}
