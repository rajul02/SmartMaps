package com.example.rajulnahar.smartmaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajul Nahar on 24-01-2017.
 */

public class SmartMapsdb extends SQLiteOpenHelper{

    public static String DatabaseName = "Smartmaps.db";


    public static String Table_Listedplace = "ListedPlaces";
    public static String Col_Listedplace_Id = "id";
    public static String Col_Listedplace_Categories = "categories";
    public static String Col_Listedplace_Latitude = "latitude";
    public static String Col_Listedplace_Longitude = "longitude";
    public static String Col_Listedplace_Comments = "comments";
    public static String Col_Listedplace_Image = "image";

    public static String Table_Favourites = "Favourites";
    public static String Col_Favourites_Id = "id";
    public static String Col_Favourites_Latitude = "latitude";
    public static String Col_Favourites_Longitude = "longitude";

    public static String Table_Categories = "Categories";
    public static String Col_Categories_Id = "id";
    public static String Col_Categories_category = "category";

    public String createTableListedplace = "Create table "+Table_Listedplace+"(" +
             Col_Listedplace_Id+" integer primary key autoincrement, " +
            Col_Listedplace_Categories+" text," +
           Col_Listedplace_Latitude+ " text," +
            Col_Listedplace_Longitude+" text," +
            Col_Listedplace_Comments+" text," +
            Col_Listedplace_Image +" text" + ")";

    public String createTableFavourites = "Create table "+Table_Favourites+"(" +
            Col_Favourites_Id+" integer primary key autoincrement, " +
            Col_Favourites_Latitude+ " text," +
            Col_Favourites_Longitude+" text" + ")";

    public String createTableCategories = "Create table "+Table_Categories+"("+
            Col_Categories_Id+ " integer primary key autoincrement, "+
            Col_Categories_category+ " text)";

    public SQLiteDatabase sqLiteDatabase;
    private static SmartMapsdb mInstance = null;
    private String sql;
    public static SmartMapsdb getInstance(Context context){
        if(mInstance == null){
            mInstance = new SmartMapsdb(context);
        }
        return mInstance;
    }

    private SmartMapsdb(Context context) {
        super(context, DatabaseName,null, 1);
        sqLiteDatabase = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(createTableListedplace);
        db.execSQL(createTableFavourites);
        db.execSQL(createTableCategories);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "Drop Table "+Table_Listedplace;
        db.execSQL(sql);
        sql = "Drop Table "+Table_Favourites;
        db.execSQL(sql);
        sql = "Drop Table "+Table_Categories;
        db.execSQL(sql);
        onCreate(db);

    }

    public List<ListedPlace> getListedlace(){
        List<ListedPlace> listedPlaces = new ArrayList<>();
        sql = "select * from " + Table_Listedplace;
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                listedPlaces.add(setListedPlaceFromCursor(cursor));
            }while (cursor.moveToNext());
        }
        return listedPlaces;
    }

    public ListedPlace setListedPlaceFromCursor(Cursor cursor){
        ListedPlace listedPlace = new ListedPlace();
        listedPlace.id = cursor.getInt(cursor.getColumnIndex(Col_Listedplace_Id));
        listedPlace.longitude = cursor.getString(cursor.getColumnIndex(Col_Listedplace_Longitude));
        listedPlace.latitude = cursor.getString(cursor.getColumnIndex(Col_Listedplace_Latitude));
        listedPlace.category = cursor.getString(cursor.getColumnIndex(Col_Listedplace_Categories));
        listedPlace.comments = cursor.getString(cursor.getColumnIndex(Col_Listedplace_Comments));
        listedPlace.image = cursor.getString(cursor.getColumnIndex(Col_Listedplace_Image));
        return listedPlace;
    }

    public long addListedPlace(ListedPlace listedPlace){
        return sqLiteDatabase.insertOrThrow(Table_Listedplace,null,getValues(listedPlace));
    }

    public ContentValues getValues(ListedPlace listedPlace){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Listedplace_Categories,listedPlace.category);
        contentValues.put(Col_Listedplace_Comments,listedPlace.comments);
        //contentValues.put(Col_Listedplace_Id,listedPlace.id);
        contentValues.put(Col_Listedplace_Image,listedPlace.image);
        contentValues.put(Col_Listedplace_Latitude,listedPlace.latitude);
        contentValues.put(Col_Listedplace_Longitude,listedPlace.longitude);
        return contentValues;
    }

    public long addFavourites(Favourites favourites){
        return sqLiteDatabase.insertOrThrow(Table_Favourites,null,getValuesFavourites(favourites));
    }

    public ContentValues getValuesFavourites(Favourites favourites){
        ContentValues contentValues = new ContentValues();
        //contentValues.put(Col_Favourites_Id,favourites.id);
        contentValues.put(Col_Favourites_Latitude,favourites.latitude);
        contentValues.put(Col_Favourites_Longitude,favourites.longitude);
        return contentValues;
    }

    public int getFavouriteByLatLong(double latitude,double longitude){
        String sql = "select * from "+Table_Favourites+" where "+Col_Favourites_Latitude +" = ? and "+ Col_Favourites_Longitude + " = ? ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,new String[]{String.valueOf(latitude), String.valueOf(longitude)});
        if(cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(Col_Favourites_Id));
        }
        return -1;

    }

    public int removeFavouriteById(int id){
        return sqLiteDatabase.delete(Table_Favourites,Col_Favourites_Id +" = ?",new String[]{String.valueOf(id)});

    }

    public long addCategory(Categories categories){
        return sqLiteDatabase.insertOrThrow(Table_Categories,null,getValuesCategories(categories));
    }

    public ContentValues getValuesCategories(Categories categories){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Categories_Id,categories.id);
        contentValues.put(Col_Categories_category,categories.category);
        return contentValues;
    }

    public void testCategory(){
        sql = "insert into " + Table_Categories + "("+Col_Categories_category+") values('Temple')";
        sqLiteDatabase.execSQL(sql);
        sql = "insert into " + Table_Categories + "("+Col_Categories_category+") values('Food')";
        sqLiteDatabase.execSQL(sql);
        sql = "insert into " + Table_Categories + "("+Col_Categories_category+") values('ATM')";
        sqLiteDatabase.execSQL(sql);
        sql = "insert into " + Table_Categories + "("+Col_Categories_category+") values('Banks')";
        sqLiteDatabase.execSQL(sql);
        sql = "insert into " + Table_Categories + "("+Col_Categories_category+") values('airport')";
        sqLiteDatabase.execSQL(sql);
    }

    public Categories getCategoriesFromCursor(Cursor cursor){
        Categories categories = new Categories();
        categories.id = cursor.getInt(cursor.getColumnIndex(Col_Categories_Id));
        categories.category = cursor.getString(cursor.getColumnIndex(Col_Categories_category));
        return categories;
    }

    public List<Categories> getAllCategories(){
        List<Categories> categories = new ArrayList<>();
        String sql = "Select * from "+ Table_Categories;
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do{
                categories.add(getCategoriesFromCursor(cursor));
                Log.e("category",getCategoriesFromCursor(cursor).category);
            }while (cursor.moveToNext());
        }
        return categories;
    }

    public void deleteTables(){
        SQLiteDatabase db = sqLiteDatabase;
        String sql = "Drop Table if exists "+Table_Listedplace;
        db.execSQL(sql);
        sql = "Drop Table if exists "+Table_Favourites;
        db.execSQL(sql);
        sql = "Drop Table if exists "+Table_Categories;
        db.execSQL(sql);
        onCreate(db);
    }
}
