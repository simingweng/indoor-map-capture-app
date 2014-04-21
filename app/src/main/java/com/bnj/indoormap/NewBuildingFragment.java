package com.bnj.indoormap;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bnj.indoormap.utils.Constants;
import com.bnj.indoortms.api.client.model.Building;
import com.bnj.indoortms.api.client.model.Location;
import com.bnj.indoortms.api.client.request.CreateBuildingRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link NewBuildingFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link NewBuildingFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class NewBuildingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = NewBuildingFragment.class.getName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SEARCH_PLACE_REQUEST = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Address address;

    private SpiceManager spiceManager = new SpiceManager(
            GsonGoogleHttpClientSpiceService.class);

    private OnFragmentInteractionListener mListener;

    public NewBuildingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using
     * the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewBuildingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewBuildingFragment newInstance(String param1, String param2) {
        NewBuildingFragment fragment = new NewBuildingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_building, container,
                false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
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
            case R.id.action_save:
                Building newBuilding = new Building();
                newBuilding.setName(address.getFeatureName());
                newBuilding.setFormatted_address(address.getAddressLine(0));
                newBuilding.setReference(address.getExtras().getString(
                        Constants.PlaceSearch.PLACE_REFERENCE_EXTRA_KEY));
                Location location = new Location();
                location.lat = address.getLatitude();
                location.lng = address.getLongitude();
                newBuilding.setLocation(location);
                String usertoken = PreferenceManager.getDefaultSharedPreferences(
                        getActivity()).getString(Constants.PrefsKeys.USER_TOKEN,
                        null);
                CreateBuildingRequest request = new CreateBuildingRequest(
                        newBuilding, usertoken);
                spiceManager.execute(request, new RequestListener<Building>() {

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(),
                                "failed to create new building on server",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRequestSuccess(Building result) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(),
                                "new building created on server successfully",
                                Toast.LENGTH_LONG).show();
                    }

                });
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.mapButton).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Constants.PlaceSearch.SEARCH_PLACE_ACTION);
                        if (address != null) {
                            intent.putExtra(Constants.PlaceSearch.INITIAL_LOCATION_EXTRA_KEY,
                                    address);
                        }
                        startActivityForResult(intent, SEARCH_PLACE_REQUEST);
                    }

                }
        );
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
        switch (requestCode) {
            case SEARCH_PLACE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    address = data.getParcelableExtra(Constants.PlaceSearch.ADDRESS_EXTRA_KEY);
                    EditText name = (EditText) getView().findViewById(
                            R.id.editTextName);
                    name.setText(address.getFeatureName());
                    EditText formatted_address = (EditText) getView().findViewById(
                            R.id.editTextAddress);
                    formatted_address.setText(address.getAddressLine(0));
                    EditText lng = (EditText) getView().findViewById(
                            R.id.editTextLng);
                    lng.setText(String.valueOf(address.getLongitude()));
                    EditText lat = (EditText) getView().findViewById(
                            R.id.EditTextLat);
                    lat.setText(String.valueOf(address.getLatitude()));

                    // since we have the detail information of the place
                    // chosen, we can use the coordinates to query a static google
                    // map image to show in the image button view. Here we use the
                    // Universal Image Loader library for querying and loading it
                    ImageLoader.getInstance().displayImage(
                            String.format(Constants.API_URLs.GOOLE_STATIC_MAP_IMAGE,
                                    address.getLatitude(), address.getLongitude(),
                                    address.getLatitude(), address.getLongitude()),
                            (ImageView) getView().findViewById(R.id.mapButton)
                    );
                }
                break;
        }
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
        inflater.inflate(R.menu.new_building, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated to
     * the activity and potentially other fragments contained in that activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
