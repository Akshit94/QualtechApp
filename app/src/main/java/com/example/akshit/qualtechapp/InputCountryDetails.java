package com.example.akshit.qualtechapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.akshit.qualtechapp.data.CountryDBHelper;
import com.example.akshit.qualtechapp.data.DetailsContract;

public class InputCountryDetails extends AppCompatActivity {

    public static final String UPDATE_FLAG = "update_db";

    TextInputEditText countryName;
    TextInputEditText capitalName;
    TextInputEditText regionName;
    TextInputEditText subRegion;
    TextInputEditText population;
    TextInputEditText latitude;
    TextInputEditText longitude;
    TextInputEditText countryCode;
    TextInputEditText languages;
    Intent intent;
    CountryItem countryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_country_details);
        setTitle("Input Country Details");

        countryName = (TextInputEditText) findViewById(R.id.country_name);
        capitalName = (TextInputEditText) findViewById(R.id.capital_name);
        regionName = (TextInputEditText) findViewById(R.id.region);
        subRegion = (TextInputEditText) findViewById(R.id.sub_region);
        population = (TextInputEditText) findViewById(R.id.population);
        latitude = (TextInputEditText) findViewById(R.id.latitude_add);
        longitude = (TextInputEditText) findViewById(R.id.longitude_add);
        countryCode = (TextInputEditText) findViewById(R.id.country_code_add);
        languages = (TextInputEditText) findViewById(R.id.languages_add);
        intent = getIntent();

        if (intent != null && intent.hasExtra(UPDATE_FLAG)) {
            countryItem = intent.getParcelableExtra(UPDATE_FLAG);
            countryName.setText(countryItem.name);
            capitalName.setText(countryItem.capital);
            countryCode.setText(countryItem.countryCode);
            languages.setText(countryItem.languages);
            regionName.setText(countryItem.region);
            subRegion.setText(countryItem.subregion);
            latitude.setText(Double.toString(countryItem.latitude));
            longitude.setText(Double.toString(countryItem.longitude));
            population.setText(Integer.toString(countryItem.population));
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_country, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
            SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();

            ContentValues countryValues = returnValues();

            if (intent != null && intent.hasExtra(UPDATE_FLAG)) {
                sqLiteDatabase.update(DetailsContract.CountryEntry.TABLE_NAME,
                        countryValues,
                        DetailsContract.CountryEntry.COUNTRY_NAME + " = ?",
                        new String[]{countryItem.name});
                Toast.makeText(getApplicationContext(), "Country Updated!", Toast.LENGTH_SHORT).show();
            } else {

                Cursor retCursor = sqLiteDatabase.query(DetailsContract.CountryEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                int flag = 0;
                while (retCursor.moveToNext()) {
                    if (retCursor.getString(1).equals(countryName.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Country Already Exists!", Toast.LENGTH_SHORT).show();
                        flag = 1;
                        break;
                    }
                }

                if (flag == 0) {
                    sqLiteDatabase.insert(DetailsContract.CountryEntry.TABLE_NAME, null, countryValues);
                    Toast.makeText(getApplicationContext(), "Country Added to the Database!", Toast.LENGTH_SHORT).show();
                }

                retCursor.close();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues returnValues() {
        ContentValues countryValues = new ContentValues();

        countryValues.put(DetailsContract.CountryEntry.COUNTRY_NAME, countryName.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.COUNTRY_CODE, countryCode.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.CAPITAL, capitalName.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.REGION, regionName.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.SUBREGION, subRegion.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.LANGUAGES, languages.getText().toString());
        countryValues.put(DetailsContract.CountryEntry.POPULATION, Integer.parseInt(population.getText().toString()));
        countryValues.put(DetailsContract.CountryEntry.LATITUDE, Double.parseDouble(latitude.getText().toString()));
        countryValues.put(DetailsContract.CountryEntry.LONGITUDE, Double.parseDouble(longitude.getText().toString()));
        countryValues.put(DetailsContract.CountryEntry.AREA, 0.0f);
        countryValues.put(DetailsContract.CountryEntry.FLAG_URL, " ");

        return countryValues;
    }
}
