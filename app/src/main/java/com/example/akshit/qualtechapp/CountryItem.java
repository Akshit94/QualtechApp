package com.example.akshit.qualtechapp;

import android.os.Parcel;
import android.os.Parcelable;

class CountryItem implements Parcelable {
    public static final Creator<CountryItem> CREATOR = new Creator<CountryItem>() {
        @Override
        public CountryItem createFromParcel(Parcel in) {
            return new CountryItem(in);
        }

        @Override
        public CountryItem[] newArray(int size) {
            return new CountryItem[size];
        }
    };
    String name;
    String capital;
    String region;
    String subregion;
    int population;
    double latitude;
    double longitude;
    double area;
    String countryCode;
    String languages;
    String flagUrl;

    CountryItem(String name, String capital, String region, String subregion, int population, double latitude,
                double longitude, double area, String countryCode, String languages, String flagUrl) {

        this.name = name;
        this.capital = capital;
        this.region = region;
        this.subregion = subregion;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.countryCode = countryCode;
        this.languages = languages;
        this.flagUrl = flagUrl;

    }

    CountryItem(Parcel in) {
        name = in.readString();
        capital = in.readString();
        region = in.readString();
        subregion = in.readString();
        population = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        area = in.readDouble();
        countryCode = in.readString();
        languages = in.readString();
        flagUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(capital);
        dest.writeString(region);
        dest.writeString(subregion);
        dest.writeInt(population);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(area);
        dest.writeString(countryCode);
        dest.writeString(languages);
        dest.writeString(flagUrl);
    }

}
