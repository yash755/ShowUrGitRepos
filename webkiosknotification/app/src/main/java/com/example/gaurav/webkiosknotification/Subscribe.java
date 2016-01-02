package com.example.gaurav.webkiosknotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pushbots.push.Pushbots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Subscribe extends AppCompatActivity{

    DatabaseHelper db;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       db = new DatabaseHelper(this);
        ImageView i1 = (ImageView)findViewById(R.id.avatar);
        pd = new ProgressDialog(this);
        pd.setMessage("Downloading Avatar");



        Cursor cr = db.getList();


        cr.moveToFirst();
        final ArrayList<String> reponame = new ArrayList<>();
        while (!cr.isAfterLast()) {
            reponame.add(cr.getString(cr.getColumnIndex("name")));
            cr.moveToNext();

        }

        cr.moveToFirst();
        final ArrayList<String> avatar = new ArrayList<>();
        while (!cr.isAfterLast()) {
            avatar.add(cr.getString(cr.getColumnIndex("avatar_url")));
            cr.moveToNext();

        }

        cr.moveToFirst();
        final ArrayList<String> forks = new ArrayList<>();
        while (!cr.isAfterLast()) {
            forks.add(cr.getString(cr.getColumnIndex("forks_count")));
            cr.moveToNext();

        }

        cr.moveToFirst();
        final ArrayList<String> watchers = new ArrayList<>();
        while (!cr.isAfterLast()) {
            watchers.add(cr.getString(cr.getColumnIndex("watchers_count")));
            cr.moveToNext();

        }

        cr.moveToFirst();
        final ArrayList<String> fullname = new ArrayList<>();
        while (!cr.isAfterLast()) {
           fullname.add(cr.getString(cr.getColumnIndex("full_name")));
            cr.moveToNext();

        }


        if(isConnected()) {
            String bannerpath = avatar.get(0).toString();
            new DownloadImageTask(i1).execute(bannerpath);
        }
        else
            Toast.makeText(getApplicationContext(), "Avatar can't be displayed as no Internet Connection!!!", Toast.LENGTH_SHORT).show();

        cr.close();


        ListAdapter adpt = new Custom(this,reponame,forks,watchers);
        final ListView li = (ListView) findViewById(R.id.lv);
        li.setAdapter(adpt);



    }



    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd.show();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            pd.dismiss();
            bmImage.setImageBitmap(result);
        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }



}
