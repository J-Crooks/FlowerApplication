package com.example.flowerapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FlowerCollection extends Fragment implements FlowerItemAdapator.OnItemListener {
    private FlowerItemAdapator adapator;
    private List<FlowerItem> items;
    public FlowerCollection() {}

    public static FlowerCollection newInstance() {
        FlowerCollection fragment = new FlowerCollection();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flower_collection, container, false);
        RecyclerView rc = v.findViewById(R.id.flowers);
        TextView noFlowers = v.findViewById(R.id.noFlowerTxt);

        items = getData();

        //If database has now items make "you have no flowers" to visible
        //or assigns list to adaptor and what display type to use
        if (items.size() == 0) {
            noFlowers.setVisibility(View.VISIBLE);
        } else {
            rc.setAdapter(adapator);
            rc.setLayoutManager(new LinearLayoutManager(this.getContext()));
        }
        return v;
    }

    private List<FlowerItem> getData() {
        //Gets all the flowers the user has from the database
        FlowerCollectionDB.FeedReaderDbHelper myFlowersHelper =  new FlowerCollectionDB.FeedReaderDbHelper(getContext());
        SQLiteDatabase db = myFlowersHelper.getReadableDatabase();

        //Columns to return values of
        String[] projection = {
                BaseColumns._ID,
                FlowerCollectionDB.FeedEntry.COL_IMAGE,
                FlowerCollectionDB.FeedEntry.COL_FLOWER,
        };

        //Performs query of database, rest are null to return all rows
        Cursor c = db.query(
                FlowerCollectionDB.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        //iterates over each rows from query and creates a floweritem of the data to append to list
        List items = new ArrayList<>();
        while(c.moveToNext()) {
            FlowerItem item = new FlowerItem(
                    c.getLong(c.getColumnIndexOrThrow(FlowerCollectionDB.FeedEntry._ID)),
                    c.getString(c.getColumnIndexOrThrow(FlowerCollectionDB.FeedEntry.COL_IMAGE)),
                    c.getString(c.getColumnIndexOrThrow(FlowerCollectionDB.FeedEntry.COL_FLOWER))
            ); items.add(item); }

        adapator = new FlowerItemAdapator(items, this);
        c.close();
        db.close();
        myFlowersHelper.close();
        return items;
    }

    @Override
    public void onItemClick(int position) {
        FlowerDisplay n = FlowerDisplay.newInstance(items.get(position));
        getFragmentManager().beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
    }
}