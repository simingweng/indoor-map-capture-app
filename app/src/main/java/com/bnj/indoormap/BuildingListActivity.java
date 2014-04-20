package com.bnj.indoormap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bnj.indoormap.floor.FloorFragment;
import com.bnj.indoormap.utils.Constants;

/**
 * An activity representing a list of Buildings. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link BuildingDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link BuildingListFragment} and the item details (if present) is a
 * {@link BuildingDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link BuildingListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class BuildingListActivity extends FragmentActivity implements
        BuildingListFragment.Callbacks, BuildingInfoFragment.OnBuildingInfoInteractionListener,
        FloorFragment.OnFloorSelectionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MenuItem signinItem;
    private MenuItem signoutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_list);

        if (findViewById(R.id.building_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((BuildingListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.building_list))
                    .setActivateOnItemClick(true);
        }

        attemptSignIn();
    }

    /**
     * Callback method from {@link BuildingListFragment.Callbacks} indicating
     * that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String buildingId) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BuildingDetailFragment.ARG_BUILDING_ID, buildingId);
            BuildingDetailFragment fragment = new BuildingDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.building_detail_container, fragment).commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, BuildingDetailActivity.class);
            detailIntent.putExtra(BuildingDetailFragment.ARG_BUILDING_ID, buildingId);
            startActivity(detailIntent);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        switch (arg0) {
            case Constants.ActivityRequestCode.LOGIN_REQUEST:
                if (arg1 == RESULT_OK) {
                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(this);
                    prefs.edit()
                            .putString(
                                    Constants.PrefsKeys.USER_NAME,
                                    arg2.getStringExtra(Constants.Login.USER_NAME_EXTRA_KEY))
                            .putString(
                                    Constants.PrefsKeys.USER_TOKEN,
                                    arg2.getStringExtra(Constants.Login.USER_TOKEN_EXTRA_KEY))
                            .commit();
                    signinItem.setVisible(false);
                    signoutItem.setVisible(true);
                    Fragment buildingListFragment = getSupportFragmentManager()
                            .findFragmentById(R.id.building_list);
                    if (buildingListFragment instanceof Reloadable) {
                        ((Reloadable) buildingListFragment).reload(true);
                    }
                }
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.building, menu);
        signinItem = menu.findItem(R.id.signin);
        signoutItem = menu.findItem(R.id.signout);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String usertoken = prefs
                .getString(Constants.PrefsKeys.USER_TOKEN, null);
        signinItem.setVisible(usertoken == null);
        signoutItem.setVisible(usertoken != null);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signin:
                attemptSignIn();
                break;
            case R.id.signout:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefs.edit().remove(Constants.PrefsKeys.USER_TOKEN).commit();
        signinItem.setVisible(true);
        signoutItem.setVisible(false);
        Fragment buildingListFragment = getSupportFragmentManager()
                .findFragmentById(R.id.building_list);
        if (buildingListFragment instanceof Reloadable) {
            ((Reloadable) buildingListFragment).unload();
        }
        if (mTwoPane) {
            // TODO also clear the detail pane
        }
    }

    private void attemptSignIn() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String usertoken = prefs
                .getString(Constants.PrefsKeys.USER_TOKEN, null);
        if (usertoken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(Constants.Login.USER_NAME_EXTRA_KEY,
                    prefs.getString(Constants.PrefsKeys.USER_NAME, null));
            startActivityForResult(intent,
                    Constants.ActivityRequestCode.LOGIN_REQUEST);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFloorSelected(String id) {

    }
}
