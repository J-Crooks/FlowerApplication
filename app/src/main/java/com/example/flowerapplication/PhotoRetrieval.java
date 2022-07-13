package com.example.flowerapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class PhotoRetrieval extends Fragment {
    private static final int PERMISSION_REQUEST = 0;
    private static final int TAKE_IMAGE = 1;
    private static final int LOAD_IMAGE = 2;
    private ImageView previewPic;
    private Button takePic;
    private Button importPic;
    private HelperFunctions helper;

    public PhotoRetrieval() {
        // Required empty public constructor
    }

    public static PhotoRetrieval newInstance() {
        PhotoRetrieval fragment = new PhotoRetrieval();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new HelperFunctions();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}; //Permissions to ask user
            requestPermissions(permission, PERMISSION_REQUEST); //Launch permissions
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_photo_retrieval, container, false);
        takePic = view.findViewById(R.id.takePhoto);
        importPic = view.findViewById(R.id.importPhoto);
        previewPic = view.findViewById(R.id.previewFlower);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Launch device camera
                startActivityForResult(intent, TAKE_IMAGE);
            }
        });

        importPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //Launch device storage to get photo
                intent.setType("image/*"); //Specify file type
                startActivityForResult(intent, LOAD_IMAGE);
            }
        });
        return  view;
    }

    public void gotoCheck(Bitmap image) {
        //Opens next PhotoCheck fragment, with bitmap as parameter
        PhotoCheck n = PhotoCheck.newInstance(helper.ConvertBitmaptoEncodedString(image));
        getFragmentManager().beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Bitmap image;
            Log.d("TEST", Integer.toString(resultCode));
            switch(requestCode) {
                case TAKE_IMAGE: //Gets bitmap from intent, after camera usage
                    Bundle extras = data.getExtras();
                    image = (Bitmap) extras.get("data");
                    gotoCheck(image);
                    break;
                case LOAD_IMAGE: //Gets bitmap from intent, after storage retrieval
                    Uri selectedImage = data.getData();
                    try {
                        //Attempt to get bitmap from URI path from storage, otherwise catch error
                        image = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), selectedImage);
                        if (image != null) { gotoCheck(image); }
                    } catch (IOException e) {}
                    break;
                default:
                    break;

            }
        }
    }
}