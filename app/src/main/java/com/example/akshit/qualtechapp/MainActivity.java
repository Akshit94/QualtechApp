package com.example.akshit.qualtechapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.akshit.qualtechapp.data.CountryDBHelper;
import com.example.akshit.qualtechapp.data.DetailsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String API_REQUEST_SHARED_PREF = "api_flag";
    public static final String BASE_API_URL = "https://restcountries.eu/rest/v2/all";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    ListView countryListView;
    CountryAdapter mCountryAdapter;
    SharedPreferences sharedPreferences;
    FloatingSearchView mFloatingSearchView;
    LinearLayout emptyView;
    TabLayout tabLayout;
    int mPageNumber = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryListView = (ListView) findViewById(R.id.country_list_view);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        countryListView.setEmptyView(emptyView);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Previous Page"), 0);
        tabLayout.addTab(tabLayout.newTab().setText("Next Page"), 1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (sharedPreferences.getInt(API_REQUEST_SHARED_PREF, 0) == 1)
                    pagination(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Don't need to do anything when the tab is unselected.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (sharedPreferences.getInt(API_REQUEST_SHARED_PREF, 0) == 1)
                    pagination(tab.getPosition());
            }
        });


        mCountryAdapter = new CountryAdapter(getApplicationContext());
        countryListView.setAdapter(mCountryAdapter);

        mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mFloatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if ("".equals(newQuery))
                    new FetchDetailsTask().execute();
                else
                    new FetchDetailsTask().execute(newQuery);
            }
        });

        mFloatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.action_web) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
                    webIntent.setData(Uri.parse(BASE_API_URL));
                    startActivity(webIntent);
                } else if (id == R.id.action_reload) {
                    mCountryAdapter.clear();
                    mCountryAdapter.notifyDataSetChanged();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(API_REQUEST_SHARED_PREF, 0);
                    editor.apply();
                    mPageNumber = 10;
                    CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
                    SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
                    sqLiteDatabase.delete(DetailsContract.CountryEntry.TABLE_NAME, null, null);
                    new FetchDetailsTask().execute();
                }

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inputIntent = new Intent(getApplicationContext(), InputCountryDetails.class);
                startActivity(inputIntent);
            }
        });

        new FetchDetailsTask().execute();

    }

    public void pagination(int position) {

        if (position == 0) {

            if (mPageNumber > 10) {
                mPageNumber = mPageNumber - 10;
                new FetchDetailsTask().execute();
            } else
                Toast.makeText(getApplicationContext(), "This is the starting page!", Toast.LENGTH_SHORT).show();

        } else if (position == 1) {
            mPageNumber = mPageNumber + 10;
            new FetchDetailsTask().execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchDetailsTask().execute();
    }

    private class FetchDetailsTask extends AsyncTask<String, Integer, CountryItem[]> {

        private CountryItem[] getCountryDataFromJson(String countryJsonStr) throws JSONException {

            final String API_COUNTRY_NAME = "name";
            final String API_LANGUAGE_NAME = "name";
            final String API_CAPITAL_NAME = "capital";
            final String API_REGION = "region";
            final String API_SUBREGION = "subregion";
            final String API_POPULATION = "population";
            final String API_CODE = "alpha3Code";
            //final String API_AREA = "area";
            final String API_LATLNG = "latlng";
            final String API_LANGUAGES = "languages";
            final String API_FLAG_URL = "flag";

            JSONArray countryJsonArray = new JSONArray(countryJsonStr);
            CountryItem[] countryItems = new CountryItem[10];

            CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
            SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();

            for (int i = 0, j = 0; i < countryJsonArray.length(); ++i, ++j) {

                JSONObject countryJson = countryJsonArray.getJSONObject(i);
                String country_name = countryJson.getString(API_COUNTRY_NAME);
                String capital_name = countryJson.getString(API_CAPITAL_NAME);
                String region = countryJson.getString(API_REGION);
                String subRegion = countryJson.getString(API_SUBREGION);
                int population = countryJson.getInt(API_POPULATION);
                String countryCode = countryJson.getString(API_CODE);
                double countryArea = 0.0f;
                //double countryArea = countryJson.getDouble(API_AREA);
                String flagUrl = countryJson.getString(API_FLAG_URL);
                JSONArray languagesArray = countryJson.getJSONArray(API_LANGUAGES);
                StringBuilder languageBuilder = new StringBuilder();
                languageBuilder.append(languagesArray.getJSONObject(0).getString(API_LANGUAGE_NAME));

                for (int k = 1; k < languagesArray.length(); ++k) {
                    JSONObject language = languagesArray.getJSONObject(k);
                    languageBuilder.append(", ").append(language.getString(API_LANGUAGE_NAME));
                }

                String languagesSpoken = languageBuilder.toString();
                JSONArray geoArray = countryJson.getJSONArray(API_LATLNG);
                double latitude = 0.0f;
                double longitude = 0.0f;

                if (geoArray.length() != 0) {
                    latitude = geoArray.getDouble(0);
                    longitude = geoArray.getDouble(1);
                }

                ContentValues countryValues = new ContentValues();

                countryValues.put(DetailsContract.CountryEntry.COUNTRY_NAME, country_name);
                countryValues.put(DetailsContract.CountryEntry.CAPITAL, capital_name);
                countryValues.put(DetailsContract.CountryEntry.REGION, region);
                countryValues.put(DetailsContract.CountryEntry.SUBREGION, subRegion);
                countryValues.put(DetailsContract.CountryEntry.LATITUDE, latitude);
                countryValues.put(DetailsContract.CountryEntry.LONGITUDE, longitude);
                countryValues.put(DetailsContract.CountryEntry.COUNTRY_CODE, countryCode);
                countryValues.put(DetailsContract.CountryEntry.AREA, countryArea);
                countryValues.put(DetailsContract.CountryEntry.LANGUAGES, languagesSpoken);
                countryValues.put(DetailsContract.CountryEntry.FLAG_URL, flagUrl);
                countryValues.put(DetailsContract.CountryEntry.POPULATION, population);

                sqLiteDatabase.insert(DetailsContract.CountryEntry.TABLE_NAME, null, countryValues);

                if (j >= (mPageNumber - 10) && j < mPageNumber)
                    countryItems[j] = new CountryItem(country_name, capital_name, region, subRegion, population, latitude,
                            longitude, countryArea, countryCode, languagesSpoken, flagUrl);

            }

            return countryItems;

        }

        private CountryItem[] requestApi() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String retrievedDetails;

            try {

                URL url = new URL(BASE_API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return new CountryItem[0];
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return new CountryItem[0];
                }
                retrievedDetails = buffer.toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(API_REQUEST_SHARED_PREF, 1);
                editor.apply();

                return getCountryDataFromJson(retrievedDetails);

            } catch (IOException | JSONException e) {

                Log.e(LOG_TAG, "Error ", e);

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {

                        reader.close();

                    } catch (final IOException e) {

                        Log.e(LOG_TAG, "Error closing stream", e);

                    }
                }
            }
            return new CountryItem[0];

        }

        private CountryItem[] loadOrQuery(Cursor retCursor, String... params) {

            if (params.length > 0) {

                CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = mOpenHelper.getReadableDatabase();

                Cursor queryCursor = sqLiteDatabase.query(DetailsContract.CountryEntry.TABLE_NAME, null,
                        DetailsContract.CountryEntry.COUNTRY_NAME + " LIKE '" + params[0] + "%'",
                        null, null, null, null);
                CountryItem[] countryItems = new CountryItem[queryCursor.getCount()];

                int i = 0;
                while (queryCursor.moveToNext() && i < queryCursor.getCount()) {

                    countryItems[i] = new CountryItem(queryCursor.getString(1),
                            queryCursor.getString(2),
                            queryCursor.getString(3),
                            queryCursor.getString(4),
                            queryCursor.getInt(11),
                            queryCursor.getDouble(5),
                            queryCursor.getDouble(6),
                            queryCursor.getDouble(8),
                            queryCursor.getString(7),
                            queryCursor.getString(9),
                            queryCursor.getString(10));
                    ++i;
                }

                queryCursor.close();
                return countryItems;

            } else {

                CountryItem[] countryItems = new CountryItem[10];
                int i = 0;
                int p = mPageNumber - 10;

                while (retCursor.moveToNext()) {

                    int position = retCursor.getPosition();
                    if (position == p && p < mPageNumber) {
                        countryItems[i] = new CountryItem(retCursor.getString(1),
                                retCursor.getString(2),
                                retCursor.getString(3),
                                retCursor.getString(4),
                                retCursor.getInt(11),
                                retCursor.getDouble(5),
                                retCursor.getDouble(6),
                                retCursor.getDouble(8),
                                retCursor.getString(7),
                                retCursor.getString(9),
                                retCursor.getString(10));
                        ++i;
                        ++p;
                    }

                }

                retCursor.close();
                return countryItems;
            }

        }

        @Override
        protected CountryItem[] doInBackground(String... params) {

            if (sharedPreferences.getInt(API_REQUEST_SHARED_PREF, 0) == 0) {

                return requestApi();

            } else {

                CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();

                Cursor retCursor = sqLiteDatabase.query(DetailsContract.CountryEntry.TABLE_NAME,
                        null, null, null, null, null, null);
                int cursorCount = retCursor.getCount();

                if (mPageNumber > cursorCount) {

                    mPageNumber = mPageNumber - 10;
                    publishProgress(1);
                    return new CountryItem[0];

                } else {
                    return loadOrQuery(retCursor, params);
                }

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == 1) {
                Toast.makeText(getApplicationContext(), "This is the last page!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(CountryItem[] countryItems) {
            super.onPostExecute(countryItems);
            if (countryItems.length != 0) {
                mCountryAdapter.clear();
                for (CountryItem countryItem : countryItems) {
                    mCountryAdapter.add(countryItem);
                }
            }
            mCountryAdapter.notifyDataSetChanged();
        }
    }
}
