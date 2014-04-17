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
 * {@link BuildingInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuildingInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuildingInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = BuildingInfoFragment.class.getName();
    private static final String staticMapBaseUrl = "http://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=18&size=540x480&markers=%f,%f&sensor=true&key=AIzaSyDpryIy62fGHzSSFjnYlsVTXTTWEm1aZ6c";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);
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
            ImageLoader.getInstance().displayImage(String.format(staticMapBaseUrl, building.getLocation().lat, building.getLocation().lng, building.getLocation().lat, building.getLocation().lng), image);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuildingInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuildingInfoFragment newInstance(String param1, String param2) {
        BuildingInfoFragment fragment = new BuildingInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BuildingInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mParam1 != null) {
            GetBuildingByIdRequest request = new GetBuildingByIdRequest(mParam1);
            spiceManager.execute(request, request.getCacheKey(), DurationInMillis.ONE_HOUR, listener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        spiceManager.shouldStop();
        super.onDestroy();
    }


}
