package com.example.akshit.qualtechapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class CountryAdapter extends BaseAdapter {

    private Context mContext;
    private List<CountryItem> list = new ArrayList<>();

    CountryAdapter(Context c) {
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.country_list_item, parent, false);
        }

        TextView countryName = (TextView) convertView.findViewById(R.id.country_name_list_item);
        TextView countryCode = (TextView) convertView.findViewById(R.id.country_code_list_item);
        TextView capital = (TextView) convertView.findViewById(R.id.capital_name_list_item);
        TextView region = (TextView) convertView.findViewById(R.id.region_name_list_item);
        ImageView flag = (ImageView) convertView.findViewById(R.id.country_flag_image);
        CardView item_card = (CardView) convertView.findViewById(R.id.card_view_list_item);

        CountryItem countryItem = list.get(position);
        countryName.setText(countryItem.name);
        capital.setText(countryItem.capital);
        region.setText(countryItem.region);
        countryCode.setText("(" + countryItem.countryCode + ")");

        GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder = Glide.with(mContext)
                .using(Glide.buildStreamModelLoader(Uri.class, mContext), InputStream.class)
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
                .into(flag);

        item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, DetailActivity.class);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                detailIntent.putExtra("COUNTRY_OBJECT_PARCELABLE_EXTRA", (Parcelable) getItem(position));
                mContext.startActivity(detailIntent);
            }
        });

        return convertView;
    }

    void add(CountryItem countryItem) {
        list.add(countryItem);
    }

    void clear() {
        list.clear();
    }
}
