package com.example.flowerapplication;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FlowerInformationDB extends  SQLiteOpenHelper {
    private static String DB_PATH = "";
    private static final String DB_NAME = "Flowers.db";
    private static final String TABLE_NAME = "FlowerInfo";

    private final Context ctx;

    private SQLiteDatabase myDataBase;
    private SQLiteOpenHelper sqLiteOpenHelper;

    private static final String COL_SUN = "SUNLIGHT";
    private static final String COL_ID = "ID";
    private static final String COL_WATER = "WATER";
    private static final String COL_FLOWER = "NAME";

    public FlowerInformationDB(Context ctx) {
        super(ctx, DB_NAME, null, 1);
        this.ctx = ctx;
        DB_PATH = ctx.getDatabasePath(DB_NAME).toString();

        try {
            createDataBase();
            openDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getColSun() {
        return COL_SUN;
    }

    public String getColWater() {
        return COL_WATER;
    }

    public void createDataBase() throws IOException { //Creates a database from a certain file
        boolean dbExist = checkDataBase(); //Checks if database has already been created
        if(dbExist) { }
        else {
            this.getWritableDatabase();
            try { //In case file does not exist
                copyDataBase(); //Copies bytes .db to application database location
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public boolean checkDataBase() {
        //Attempts to open database from application location, returning true if database exists
        //else returns false if doesn't
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) { }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        //Copies bytes from file to specified location in application
        InputStream myInput = ctx.getAssets().open(DB_NAME);
        String outFileName = DB_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws IOException {
        //opens database to myDataBase variable
        String myPath = DB_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath,null, SQLiteDatabase.OPEN_READONLY);
    }

    public Cursor getQuery(Activity act, String flower) {
        sqLiteOpenHelper = new FlowerInformationDB(act);
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();

        String[] projection = { COL_WATER, COL_SUN };
        String selection = COL_FLOWER + " = ?";
        String[] selectionArgs = { flower };
        Cursor c = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return c;
    }


    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
