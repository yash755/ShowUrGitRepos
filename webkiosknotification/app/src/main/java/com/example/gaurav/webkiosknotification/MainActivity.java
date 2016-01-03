package com.example.gaurav.webkiosknotification;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseHelper db;
    EditText name;
    Button   submit,see,update;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       name  = (EditText)findViewById(R.id.editText);
       submit= (Button)findViewById(R.id.submit);
       see   = (Button)findViewById(R.id.see);

       submit.setOnClickListener(this);
       see.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.submit:
                if (isConnected()) {
                    if (!validate())
                        Toast.makeText(getApplicationContext(), "Fill Username", Toast.LENGTH_SHORT).show();
                    else {

                        db = new DatabaseHelper(this);
                        Cursor c= db.getList();

                        if(c.getCount() != 0)
                            db.removeAll();

                            c.close();

                        String username = name.getText().toString();
                        sendnotifications(username);

                    }
                } else
                    Toast.makeText(getApplicationContext(), "You are Not Connected", Toast.LENGTH_SHORT).show();
                break;

            case R.id.see:

                db = new DatabaseHelper(this);
                Cursor c2= db.getList();

                if(c2.getCount() != 0)
                startActivity(new Intent(this,Subscribe.class));
                else
                    Toast.makeText(getApplicationContext(), "No Repo's To Show", Toast.LENGTH_SHORT).show();

                c2.close();

                break;


        }
    }




    void sendnotifications(String username)
    {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();


        String url = "https://api.github.com/users/" + username +"/repos";

        System.out.println("URL:"+ url);

        try {

        JsonArrayRequest postRequest = new JsonArrayRequest(Request.Method.GET, url,null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            inserthere(jsonArray);
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


    public void inserthere(JSONArray jsonArray){

        db = new DatabaseHelper(this);

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String name = (String) object.get("name");
                String full_name = (String)object.get("full_name");

                JSONObject js = object.getJSONObject("owner");
                String avatar_url= js.getString("avatar_url");


                Integer forks_count =    (Integer) object.get("forks_count");
                Integer watchers_count = (Integer) object.get("watchers_count");


                System.out.println("I am sucess" + name + forks_count + watchers_count + avatar_url );


                db.insertdata(name, avatar_url, forks_count, watchers_count, full_name);
            } catch (JSONException e) {
                Log.e("SAMPLE", "error getting result " + i, e);
            }
        }

    }



    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private boolean validate() {
        return !name.getText().toString().trim().equals("") ;
    }


}
