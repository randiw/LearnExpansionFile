package com.learn.expansionfile.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.learn.expansionfile.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by randiwaranugraha on 5/28/15.
 */
public class ZipFileAdapter extends ArrayAdapter {

    public static final String TAG = ZipFileAdapter.class.getSimpleName();

    private Context context;
    private ZipResourceFile.ZipEntryRO[] entries;

    public ZipFileAdapter(Context context, ZipResourceFile.ZipEntryRO[] entries) {
        super(context, R.layout.list_zip_file);
        this.context = context;
        this.entries = entries;
    }

    @Override
    public ZipResourceFile.ZipEntryRO getItem(int position) {
        return entries[position];
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_zip_file, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ZipResourceFile.ZipEntryRO entryRO = getItem(position);
        holder.filename.setText(entryRO.getZipFileName());
        holder.descriptor.setText(entryRO.getAssetFileDescriptor().toString());

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.filename) TextView filename;
        @InjectView(R.id.descriptor) TextView descriptor;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}