package com.example.akshit.qualtechapp.data;

import android.provider.BaseColumns;

public class DetailsContract {

    DetailsContract() {
    }

    public static final class CountryEntry implements BaseColumns {

        public static final String TABLE_NAME = "countries";
        public static final String COUNTRY_NAME = "name";
        public static final String CAPITAL = "capital";
        public static final String REGION = "region";
        public static final String SUBREGION = "subregion";
        public static final String POPULATION = "population";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String COUNTRY_CODE = "country_code";
        public static final String AREA = "area";
        public static final String LANGUAGES = "languages";
        public static final String FLAG_URL = "flag_url";

        CountryEntry() {
        }

    }
}
