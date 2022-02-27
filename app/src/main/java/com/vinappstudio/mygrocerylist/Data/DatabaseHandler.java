package com.vinappstudio.mygrocerylist.Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.vinappstudio.mygrocerylist.Model.Grocery;
import com.vinappstudio.mygrocerylist.Util.Constants;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context ctx;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_GROCERY_TABLE = " CREATE TABLE " + Constants.TABLE_NAME + " ("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_GROCERY_ITEM + " TEXT,"
                + Constants.KEY_QTY_NUMBER + " TEXT,"
                + Constants.KEY_DATE_NAME + " LONG );";
        sqLiteDatabase.execSQL(CREATE_GROCERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * CRUD Operations : Create , Read , Update , Delete methods
     */

    // Add Grocery
    public void addGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Its a hash map object that allow us to create key value pair that we can add to our database which is what we need
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, grocery.getDateItemAdded());

        db.insert(Constants.TABLE_NAME, null, values);
        Log.d("save", "Saved to DB");
    }

    // Get a Grocery         ,   it returns a grocery so it must b private
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("Range")
    public Grocery getGrocery(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID,
                        Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER, Constants.KEY_DATE_NAME}
                , Constants.KEY_ID + "=?", new String[]{String.valueOf(id)}
                , null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Grocery grocery = new Grocery();
        grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        grocery.setName(String.valueOf(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM))));
        grocery.setQuantity(String.valueOf(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER))));

        // convert time stamp to something readable
        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(String.valueOf(new Date(cursor.getLong(cursor.
                getColumnIndex(Constants.KEY_DATE_NAME))).getTime()));
        grocery.setDateItemAdded(formattedDate);



       // private val dateFormatter: Format = SimpleDateFormat(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMyyjjmmss"), Locale.getDefault());


        return grocery;
    }

    // Get All Groceries
    @SuppressLint("Range")
    public List<Grocery> getAllGroceries() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Grocery> groceryList = new ArrayList<>();


//        String table = Constants.TABLE_NAME;
//        String[] columns = {Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER, Constants.KEY_DATE_NAME};
//        String selection = null;
//        String[] selectionArgs = null ;
//        String groupBy = null;
//        String having = null;
//        String orderBy = Constants.KEY_DATE_NAME;
//        String limit = "DESC";
//
//        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);


        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                        Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER,
                        Constants.KEY_DATE_NAME}, null, null, null, null,
                Constants.KEY_DATE_NAME + " DESC");
        // move to First means  , there is stuff inside our cursor
        if (cursor.moveToFirst()) {
            do {
                Grocery grocery = new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));

                // convert time stamp to something readable
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                @SuppressLint("Range") String formattedDate = dateFormat.format(new Date(cursor.getLong(cursor.
                        getColumnIndex(Constants.KEY_DATE_NAME))).getTime());
                grocery.setDateItemAdded(formattedDate);
                // add to grocery list
                groceryList.add(grocery);
            } while (cursor.moveToNext());
        }

        return groceryList;
    }

    // Updated Grocery
    public int updateGrocery(@NonNull Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis()); // get system time
        // update row
        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?"
                , new String[]{String.valueOf(grocery.getId())});
    }

    // Delete Grocery
    public void deleteGrocery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();

    }

    // Get Count
    public int getGroceriesCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        Log.d("DH", " getGroceriesCount " + cursor.getCount());

        return cursor.getCount();
    }
}
