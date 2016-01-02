package com.example.gaurav.webkiosknotification;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Custom extends ArrayAdapter<String> {

    DatabaseHelper db = new DatabaseHelper(getContext());


    ArrayList<String> list1 = new ArrayList<>();
    ArrayList<String> list2 = new ArrayList<>();
    ArrayList<String> list3 = new ArrayList<>();


    Custom(Context context, ArrayList<String> name,ArrayList<String> name1,ArrayList<String> name3)
    {
        super(context, R.layout.list, name);
        list1 = name;
        list2 = name1;
        list3 = name3;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.list, parent, false);

        TextView t1  = (TextView)customView.findViewById(R.id.reponame);
        TextView t3  = (TextView)customView.findViewById(R.id.fork);
        TextView t4  = (TextView)customView.findViewById(R.id.watcher);

         t1.setText(list1.get(position));
         t3.setText(list2.get(position));
         t4.setText(list3.get(position));



        return customView;
    }









}
