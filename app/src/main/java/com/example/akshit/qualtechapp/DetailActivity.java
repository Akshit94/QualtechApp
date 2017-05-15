package com.example.akshit.qualtechapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;
import com.example.akshit.qualtechapp.data.CountryDBHelper;
import com.example.akshit.qualtechapp.data.DetailsContract;

import java.io.InputStream;

public class DetailActivity extends AppCompatActivity {

    String countryName;
    String countryCode;
    String capitalName;
    String region;
    String subRegion;
    int population;
    String languagesSpoken;
    FloatingActionButton deleteFab;
    FloatingActionButton updateFab;
    TextView tvCountryName;
    TextView tvCountryCode;
    TextView tvCapitalName;
    TextView tvRegion;
    TextView tvSubregion;
    TextView tvPopulation;
    TextView tvLanguagesSpoken;
    ImageView flagImage;
    double latitude;
    double longitude;
    CountryItem countryItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        updateFab = (FloatingActionButton) findViewById(R.id.update_fab);
        deleteFab = (FloatingActionButton) findViewById(R.id.delete_fab);
        tvCountryName = (TextView) findViewById(R.id.country_name_detail_view);
        tvCountryCode = (TextView) findViewById(R.id.country_code_detail_view);
        tvCapitalName = (TextView) findViewById(R.id.capital_name_detail_view);
        tvRegion = (TextView) findViewById(R.id.region_name_detail_view);
        tvSubregion = (TextView) findViewById(R.id.sub_region_name_detail_view);
        tvPopulation = (TextView) findViewById(R.id.population_detail_view);
        tvLanguagesSpoken = (TextView) findViewById(R.id.languages_detail_view);
        flagImage = (ImageView) findViewById(R.id.detail_flag_image);

        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra("COUNTRY_OBJECT_PARCELABLE_EXTRA")) {

            countryItem = intent.getParcelableExtra("COUNTRY_OBJECT_PARCELABLE_EXTRA");

            countryName = countryItem.name;
            setTitle(countryName);
            tvCountryName.setText(countryName);

            countryCode = countryItem.countryCode;
            tvCountryCode.setText("(" + countryCode + ")");

            region = countryItem.region;
            tvRegion.setText(region);

            subRegion = countryItem.subregion;
            tvSubregion.setText(subRegion);

            population = countryItem.population;
            tvPopulation.setText(Integer.toString(population));

            languagesSpoken = countryItem.languages;
            tvLanguagesSpoken.setText(languagesSpoken);

            capitalName = countryItem.capital;
            tvCapitalName.setText(capitalName);

            latitude = countryItem.latitude;
            longitude = countryItem.longitude;

            GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(getApplicationContext())
                    .using(Glide.buildStreamModelLoader(Uri.class, getApplicationContext()), InputStream.class)
                    .from(Uri.class)
                    .as(SVG.class)
                    .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                    .sourceEncoder(new StreamEncoder())
                    .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                    .decoder(new SvgDecoder())
                    .placeholder(R.drawable.ic_flag_black_48dp)
                    .animate(android.R.anim.fade_in)
                    .listener(new SvgSoftwareLayerSetter<Uri>());

            Uri uri = Uri.parse(countryItem.flagUrl);
            requestBuilder
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    // SVG cannot be serialized so it's not worth to cache it
                    .load(uri)
                    .into(flagImage);

        }

        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryDBHelper mOpenHelper = new CountryDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
                sqLiteDatabase.delete(DetailsContract.CountryEntry.TABLE_NAME,
                        DetailsContract.CountryEntry.COUNTRY_NAME + " = ?",
                        new String[]{countryName});
                Toast.makeText(getApplicationContext(), "Entry Deleted!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        updateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inputIntent = new Intent(getApplicationContext(), InputCountryDetails.class);
                inputIntent.putExtra(InputCountryDetails.UPDATE_FLAG, countryItem);
                startActivity(inputIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_map) {

            String label = countryName;
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=2";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
