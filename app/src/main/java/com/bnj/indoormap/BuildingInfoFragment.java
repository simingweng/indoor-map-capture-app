package com.bnj.indoormap;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bnj.indoormap.utils.Constants;
import com.bnj.indoortms.api.client.model.Building;
import com.bnj.indoortms.api.client.request.GetBuildingByIdRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.bnj.indoormap.BuildingInfoFragment.OnBuildingInfoInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildingInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = BuildingInfoFragment.class.getName();
    private RequestListener<Building> listener = new RequestListener<Building>() {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Log.e(TAG, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(Building building) {
            TextView name = (TextView) getView().findViewById(R.id.textViewName);
            name.setText(building.getName());
            TextView address = (TextView) getView().findViewById(R.id.textViewAddress);
            address.setText(building.getFormatted_address());
            ImageView image = (ImageView) getView().findViewById(R.id.imageView);
            if (building.getLocation() != null) {
                ImageLoader.getInstance().displayImage(String.format(Constants.GoogleAPI
                                .GOOGLE_STREETVIEW_IMAGE,
                        building.getLocation().lat, building.getLocation().lng
                ), image);
            }
        }
    };
    private static final String ARG_BUILDING_ID = "building_id";
    // TODO: Rename and change types of parameters
    private String buildingId;
    private OnBuildingInfoInteractionListener mListener;
    private SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);

    public BuildingInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param buildingId Parameter 1.
     * @return A new instance of fragment BuildingInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuildingInfoFragment newInstance(String buildingId) {
        BuildingInfoFragment fragment = new BuildingInfoFragment();
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
        spiceManager.start(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building_info, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnBuildingInfoInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (buildingId != null) {
            GetBuildingByIdRequest request = new GetBuildingByIdRequest(buildingId);
            spiceManager.getFromCacheAndLoadFromNetworkIfExpired(request, request.getCacheKey(),
                    DurationInMillis.ONE_MINUTE,
                    listener);
        }
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
    public interface OnBuildingInfoInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
