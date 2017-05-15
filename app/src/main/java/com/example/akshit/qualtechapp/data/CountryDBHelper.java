package com.example.akshit.qualtechapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CountryDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "countries.db";

    private static final String TEXT_NOT_NULL = " TEXT NOT NULL, ";
    private static final String REAL_NOT_NULL = " REAL NOT NULL,";

    public CountryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_COUNTRY_TABLE = "CREATE TABLE " + DetailsContract.CountryEntry.TABLE_NAME + " (" +

                DetailsContract.CountryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                DetailsContract.CountryEntry.COUNTRY_NAME + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.CAPITAL + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.REGION + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.SUBREGION + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.LATITUDE + REAL_NOT_NULL +
                DetailsContract.CountryEntry.LONGITUDE + REAL_NOT_NULL +
                DetailsContract.CountryEntry.COUNTRY_CODE + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.AREA + REAL_NOT_NULL +
                DetailsContract.CountryEntry.LANGUAGES + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.FLAG_URL + TEXT_NOT_NULL +
                DetailsContract.CountryEntry.POPULATION + " INTEGER NOT NULL" + ");";

        db.execSQL(SQL_CREATE_COUNTRY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DetailsContract.CountryEntry.TABLE_NAME);
        onCreate(db);

    }
}
