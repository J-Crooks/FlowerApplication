package com.example.flowerapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FlowerDisplay extends Fragment {

    private static final String ARG_NAME = "param1";
    private static final String ARG_IMG = "param2";
    private static final Long ARG_ID = 10L;

    private String name;
    private Long id;
    private int water;
    private int sun;

    private ImageView previewImage;
    private Button deleteFlower;
    private Bitmap image;
    private TextView waterTxtView;
    private TextView sunTxtView;

    FlowerInformationDB db;
    private HelperFunctions helper;

    public FlowerDisplay() { }
        public static FlowerDisplay newInstance(FlowerItem data) {
        FlowerDisplay fragment = new FlowerDisplay();
        Bundle args = new Bundle();
        args.putString(ARG_IMG, data.getImgEncode());
        args.putString(ARG_NAME, data.getName());
        args.putLong(String.valueOf(ARG_ID), data.getID());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new HelperFunctions();
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            id = getArguments().getLong(String.valueOf(ARG_ID));
            getFlowerData();
            image = helper.ConvertEncodedStringtoBitmap(getArguments().getString(ARG_IMG));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flower_display, container, false);
        previewImage = v.findViewById(R.id.previewFlowerDisplay);
        previewImage.setImageBitmap(image);
        deleteFlower = v.findViewById(R.id.deleteFlowerBttn);
        waterTxtView = v.findViewById(R.id.waterInfo);
        waterTxtView.setText("Water " + Integer.toString(water) + " times per week");
        sunTxtView = v.findViewById(R.id.sunInfo);
        sunTxtView.setText("Needs " + Integer.toString(sun) + " hours of sunlight every day");

        deleteFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete row from user collection database of the id providing in data item
                FlowerCollectionDB.FeedReaderDbHelper myFlowersHelper =  new FlowerCollectionDB.FeedReaderDbHelper(getContext());
                SQLiteDatabase db = myFlowersHelper.getWritableDatabase();
                db.delete(
                        FlowerCollectionDB.FeedEntry.TABLE_NAME,
                        FlowerCollectionDB.FeedEntry._ID + " = ?",
                        new String[]{Long.toString(id)}
                );
                db.close();
                myFlowersHelper.close();
                //Move to new fragment
                FlowerCollection n = FlowerCollection.newInstance();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
            }
        });
        return v;
    }

    private void getFlowerData() {
        db = new FlowerInformationDB(this.getContext());
        Cursor c = db.getQuery(getActivity(), name);
        while (c.moveToNext()) {
            sun = c.getInt(c.getColumnIndexOrThrow(db.getColSun()));
            water = c.getInt(c.getColumnIndexOrThrow(db.getColWater()));
        }
        c.close();
        db.close();
    }

}