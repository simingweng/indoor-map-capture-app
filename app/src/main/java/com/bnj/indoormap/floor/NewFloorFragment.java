package com.bnj.indoormap.floor;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bnj.indoormap.R;
import com.bnj.indoormap.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link NewFloorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFloorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Uri image;


    public NewFloorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewFloorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFloorFragment newInstance(String param1, String param2) {
        NewFloorFragment fragment = new NewFloorFragment();
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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_floor, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonFloorPlan).setOnClickListener(new
                AssignFloorPlanClickListener());
        view.findViewById(R.id.buttonGeoReference).setOnClickListener(new
                GeoReferenceClickListener());
        if (image != null) {
            ImageLoader.getInstance().displayImage(image.toString(),
                    (ImageView) getView().findViewById(R.id.imageView));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_floor_option, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.ActivityRequestCode.CAPTURE_IMAGE:
            case Constants.ActivityRequestCode.GET_EXISTING_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    image = data.getData();
                    ImageLoader.getInstance().displayImage(image.toString(),
                            (ImageView) getView().findViewById(R.id.imageView));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private class AssignFloorPlanClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_get_image_from).setItems(R.array
                    .choice_get_image_from, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(captureImageIntent, Constants.ActivityRequestCode
                                    .CAPTURE_IMAGE);
                            break;
                        case 1:
                            Intent getExistingImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getExistingImageIntent.setType("image/*");
                            startActivityForResult(getExistingImageIntent,
                                    Constants.ActivityRequestCode.GET_EXISTING_IMAGE);
                            break;
                    }
                }

            }).create().show();
        }
    }

    private class GeoReferenceClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }
}
