package com.example.gaurav.webkiosknotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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

        li.setOnTouchListener(new SwipeDetector());

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {


                if (SwipeDetector.swipeDetected()) {

                    if (SwipeDetector.getAction() == SwipeDetector.Action.LR || SwipeDetector.getAction() == SwipeDetector.Action.RL) {

                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(400);

                        String username = fullname.get(position).toString();
                        sendnotifications(username);
                        startActivity(new Intent(Subscribe.this, Subscribe.class));
                        finish();

                    }
                    else
                        Toast.makeText(Subscribe.this, "Wrong Press", Toast.LENGTH_SHORT).show();
                }
            }
        };

        li.setOnItemClickListener(listener);




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



    void sendnotifications(String username)
    {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Updating...");
        pDialog.show();


        String url = "https://api.github.com/repos/" + username ;

        System.out.println("URL:"+ url);

        try {

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            updatehere(response);
                            pDialog.hide();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Time Out Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof AuthFailureError) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Authentication Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ServerError) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NetworkError) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof ParseError) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                    }
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;

                }


            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(postRequest);

        }catch (Exception e){
            e.printStackTrace();
        }



    }


    public void updatehere(JSONObject object){

        db = new DatabaseHelper(this);
         try {

                String name = (String) object.get("name");
                String full_name = (String)object.get("full_name");

                JSONObject js = object.getJSONObject("owner");
                String avatar_url= js.getString("avatar_url");


                Integer forks_count =    (Integer) object.get("forks_count");
                Integer watchers_count = (Integer) object.get("watchers_count");


                System.out.println("I am sucess" + name + forks_count + watchers_count + avatar_url + full_name );
                db.updatedata(name, avatar_url, forks_count, watchers_count, full_name);
            } catch (JSONException e) {
                Log.e("SAMPLE", "error getting result ", e);
            }
        }





}
