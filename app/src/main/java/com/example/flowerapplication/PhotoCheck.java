package com.example.flowerapplication;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class PhotoCheck extends Fragment {
    private static final String ARG_ENCODEDIMG = "" ;
    private Bitmap image;
    private ImageView previewImage;
    private Button retakePic;
    private Button identify;
    private HelperFunctions helper;

    public PhotoCheck() {
        // Required empty public constructor
    }

    public static PhotoCheck newInstance(String codeImage) {
        PhotoCheck fragment = new PhotoCheck();
        Bundle args = new Bundle();
        args.putString(ARG_ENCODEDIMG, codeImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new HelperFunctions();
        if (getArguments() != null) {
            image = helper.ConvertEncodedStringtoBitmap(getArguments().getString(ARG_ENCODEDIMG));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_photo_check, container, false);
        previewImage = v.findViewById(R.id.cpreviewFlower);
        previewImage.setImageBitmap(image);
        retakePic = v.findViewById(R.id.Retake);
        identify = v.findViewById(R.id.identify);

        retakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoRetrieval n = PhotoRetrieval.newInstance();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
            }
        });

        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModelPredict n = ModelPredict.newInstance(getArguments().getString(ARG_ENCODEDIMG));
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
            }
        });
        return v;
    }
}