package com.bnj.indoormap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bnj.indoormap.utils.Constants;
import com.bnj.indoortms.api.client.model.Building;
import com.bnj.indoortms.api.client.request.DeleteBuildingRequest;
import com.bnj.indoortms.api.client.request.GetBuildingsRequest;
import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

/**
 * A list fragment representing a list of Buildings. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link BuildingDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class BuildingListFragment extends ListFragment implements Reloadable,
        MultiChoiceModeListener {

    private static final String TAG = BuildingListFragment.class.getName();
    private static final int NEW_BUILDING_REQUEST = 0;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private BuildingArrayAdapter buildingAdapter;
    private boolean activatedOnClick;
    private SpiceManager spiceManager = new SpiceManager(
            GsonGoogleHttpClientSpiceService.class);
    private PullToRefreshLayout mPullToRefreshLayout;
    private MenuItem addMenuItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BuildingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildingAdapter = new BuildingArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_activated_2,
                android.R.id.text1, new ArrayList<Building>());
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
                    .getInt(STATE_ACTIVATED_POSITION));
        }

        setEmptyText(getString(R.string.empty_not_sign_in));
        setListAdapter(buildingAdapter);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())

                // We need to insert the PullToRefreshLayout into the Fragment's
                // ViewGroup
                .insertLayoutInto(viewGroup)

                        // We need to mark the ListView and it's Empty View as pullable
                        // This is because they are not dirent children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                        // set the pull down distance to 25% of the view height
                .useViewDelegate(TextView.class, new ViewDelegate() {
                    @Override
                    public boolean isReadyForPull(View view, float v, float v2) {
                        return true;
                    }
                })
                .options(
                        Options.create().scrollDistance(0.25f)
                                .refreshOnUp(true).noMinimize().build()
                )
                        // a pull down refresh force a request through the network
                .listener(new OnRefreshListener() {

                    @Override
                    public void onRefreshStarted(View view) {
                        Log.i(TAG, "pull down refresh is triggered");
                        reload(true);
                    }

                })
                        // We can now complete the setup as desired
                .setup(mPullToRefreshLayout);
        /**
         * by default, we assume the fragment to work in a single pane layout,
         * so we set the list view to the standard behavior in
         * CHOICE_MODE_MULTIPLE_MODAL mode. The behavior can, later on, be
         * overridden by the method setActivateOnItemClick(boolean) if the list
         * fragment is working in a two pane layout
         */
        getListView().setMultiChoiceModeListener(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        spiceManager.start(getActivity());
        Log.i(TAG, "initially load the buildings");
        reload(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        mPullToRefreshLayout = new PullToRefreshLayout(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "stop the robospice manager");
        spiceManager.shouldStop();
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
     * android.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.building_list_option, menu);
        String usertoken = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getString(Constants.PrefsKeys.USER_TOKEN, null);
        addMenuItem = menu.findItem(R.id.add);
        addMenuItem.setVisible(usertoken != null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
     * )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(getActivity(), NewBuildingActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
                                long id) {
        super.onListItemClick(listView, view, position, id);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(buildingAdapter.getItem(position).get_id());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.

        // this boolean is the indicator about whether the list fragment is
        // working in single pane or dual pane layout
        activatedOnClick = activateOnItemClick;
        if (activatedOnClick) {
            /**
             * in two pane layout, the list view can not always be in
             * CHOICE_MODE_MULTIPLE_MODAL mode, because when CAB is not in
             * place, it must by default operate in CHOICE_MODE_SINGLE in order
             * to show an activated state background on the selected item. So,
             * we need to switch to CHOICE_MODE_MULTIPLE_MODAL manually in a
             * long click listener
             */
            getListView().setOnItemLongClickListener(
                    new OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            getListView().setChoiceMode(
                                    ListView.CHOICE_MODE_MULTIPLE_MODAL);
                            // set the long clicked item to be activated so that
                            // the CAB is triggered immediately
                            getListView().setItemChecked(position, true);
                            return true;
                        }

                    }
            );
        } else {
            getListView().setOnItemLongClickListener(null);
        }
        // when in a two pane layout, the default mode should be
        // CHOICE_MODE_SINGLE; while in single pane layout,
        // CHOICE_MODE_MULTIPLE_MODAL
        getListView().setChoiceMode(
                activatedOnClick ? ListView.CHOICE_MODE_SINGLE
                        : ListView.CHOICE_MODE_MULTIPLE_MODAL
        );
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void reload(boolean forceNetwork) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String username = prefs.getString(Constants.PrefsKeys.USER_NAME, null);
        String usertoken = prefs
                .getString(Constants.PrefsKeys.USER_TOKEN, null);
        if (username != null && usertoken != null) {
            Log.i(TAG, "load buildings with a user token");
            mPullToRefreshLayout.setRefreshing(true);
            setEmptyText(getString(R.string.empty_buildings));
            GetBuildingsRequest request = new GetBuildingsRequest(username,
                    usertoken);
            long expiry = forceNetwork ? DurationInMillis.ALWAYS_EXPIRED
                    : DurationInMillis.ONE_HOUR;
            spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request,
                    request.getCacheKey(), expiry,
                    new RequestListener<Building[]>() {

                        @Override
                        public void onRequestFailure(SpiceException arg0) {
                            Toast.makeText(getActivity(),
                                    R.string.reload_error, Toast.LENGTH_LONG)
                                    .show();
                            mPullToRefreshLayout.setRefreshComplete();
                        }

                        @Override
                        public void onRequestSuccess(Building[] arg0) {
                            Log.i(TAG, "successfully load the buildings");
                            buildingAdapter.clear();
                            buildingAdapter.addAll(arg0);
                            mPullToRefreshLayout.setRefreshComplete();
                        }

                    }
            );
        }
    }

    @Override
    public void unload() {
        Log.i(TAG, "unload the building list");
        buildingAdapter.clear();
        setEmptyText(getString(R.string.empty_not_sign_in));
        addMenuItem.setVisible(false);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // CAB is triggered, disable the pull to refresh operation
        mPullToRefreshLayout.setEnabled(false);
        mode.getMenuInflater()
                .inflate(R.menu.building_list_action_option, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteCheckedBuildings();
                mode.finish();
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mPullToRefreshLayout.setEnabled(true);
        if (activatedOnClick) {
            /**
             * when in two pane layout, the list view must switch back to
             * CHOICE_MODE_SINGLE once the CAB is finished in order to show the
             * activated state properly. But we can not invoke the mode change
             * right inside the onDestroyActionMode method, which results in a
             * stack overflow error because switching from
             * CHOICE_MODE_MULTIPLE_MODAL to CHOICE_MODE_SINGLE will call
             * onDestroyActionMode again. It becomes a infinite loop. So, we
             * need to post the mode switch to the end of the event queue and
             * let the action mode be destroyed first
             */
            getListView().post(new Runnable() {

                @Override
                public void run() {
                    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                }

            });
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                          long id, boolean checked) {
        final int checkedCount = getListView().getCheckedItemCount();
        mode.setSubtitle(getResources().getQuantityString(
                R.plurals.multiple_selection_subtitle, checkedCount,
                "building", checkedCount));
    }

    private void deleteCheckedBuildings() {
        SparseBooleanArray selectedPositions = getListView()
                .getCheckedItemPositions();
        for (int i = selectedPositions.size() - 1; i >= 0; i--) {
            final Building building = buildingAdapter.getItem(selectedPositions
                    .keyAt(i));
            Log.i(TAG, "delete " + building.getName());
            DeleteBuildingRequest request = new DeleteBuildingRequest(
                    building.get_id());
            spiceManager.execute(request, new RequestListener<Building>() {

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onRequestSuccess(Building result) {
                    Building localBuilding = null;
                    if (result != null) {
                        Log.i(TAG, result.getName()
                                + " successfully deleted on the server");
                        localBuilding = result;
                    } else {
                        Log.i(TAG, building.getName()
                                + " no longer exists on the server");
                        localBuilding = building;
                    }
                    String username = PreferenceManager
                            .getDefaultSharedPreferences(getActivity())
                            .getString(Constants.PrefsKeys.USER_NAME, null);
                    try {
                        Building[] localCachedBuildings = spiceManager
                                .getDataFromCache(Building[].class,
                                        "buildings." + username).get();
                        if (localCachedBuildings != null) {
                            List<Building> localCachedBuildingsList = new ArrayList<Building>(
                                    Arrays.asList(localCachedBuildings));
                            if (localCachedBuildingsList.remove(localBuilding)) {
                                Log.i(TAG, localBuilding.getName()
                                        + " deleted from local cache");
                                localCachedBuildingsList
                                        .toArray(localCachedBuildings);
                                spiceManager.putDataInCache(
                                        "buildings." + username,
                                        localCachedBuildings).get();
                                buildingAdapter.remove(localBuilding);
                            }
                        }
                    } catch (CacheLoadingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (CacheSavingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (CacheCreationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String buildingId);
    }

    private class BuildingArrayAdapter extends ArrayAdapter<Building> {

        public BuildingArrayAdapter(Context context, int resource,
                                    int textViewResourceId, List<Building> objects) {
            super(context, resource, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setText(getItem(position).getName());
            textView = (TextView) v.findViewById(android.R.id.text2);
            textView.setText(getItem(position).getFormatted_address());
            return v;
        }

    }
}
