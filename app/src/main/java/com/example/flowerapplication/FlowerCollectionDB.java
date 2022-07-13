package com.example.flowerapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class FlowerCollectionDB {

    public static class FeedEntry implements BaseColumns {
        //Specify a schema for database
        public static final String TABLE_NAME = "myFlowers";
        public static final String COL_FLOWER = "flower";
        public static final String COL_IMAGE = "endoced_img";
    }

    public static final String SQL_CREATE_ENTRIES =
            //Creates SQL create command
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COL_FLOWER + " FLOWER," +
                    FeedEntry.COL_IMAGE + " ENCODED_IMAGE)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            //Executes SQL command created before
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //To upgrade the schema of database
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Reverts back to an old schema of the database
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
