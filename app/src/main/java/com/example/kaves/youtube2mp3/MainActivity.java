package com.example.kaves.youtube2mp3;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.FileNameMap;
import java.util.HashMap;
import java.util.Queue;


public class MainActivity extends AppCompatActivity {
    Button QueueButton;
    Button DownloadButton;
    TextView URLToDownload;
    TextView FileName;
    TextView ResponseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QueueButton = (Button) findViewById(R.id.queue_button);
        DownloadButton = (Button) findViewById(R.id.download_button);
        URLToDownload = (TextView) findViewById(R.id.URL);
        FileName = (TextView) findViewById(R.id.FileName);
        ResponseText = (TextView) findViewById(R.id.ResponseText);

        final RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        final String urlQueue = "http://139.59.26.135:8080/queuemp3";
        final String urlDownload = "http://139.59.26.135:8080/downloadmp3";
        mRequestQueue.start();
        QueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("url", URLToDownload.getText().toString());
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, urlQueue, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    FileName.setText(response.getString("filename"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FileName.setText("That didn't work!");
                    }
                });
                mRequestQueue.add(jsObjRequest);
            }
        });
        DownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("filename", FileName.getText().toString());
                InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.POST, urlDownload,
                        new Response.Listener<byte[]>() {
                            @Override
                            public void onResponse(byte[] response) {
                                // TODO handle the response
                                try {
                                    if (response!=null) {
                                        FileOutputStream outputStream;
                                        String name=FileName.getText().toString() + ".mp3";
                                        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); ;
                                        File file = new File(downloads, name);
                                        outputStream = new FileOutputStream(file);
                                        outputStream.write(response);
                                        Log.d("Path", Environment.getDataDirectory().toString());
                                        outputStream.close();
                                        ResponseText.setText("Response: Download complete");
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                    e.printStackTrace();
                                }
                            }
                        } ,new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO handle the error
                        ResponseText.setText("Response: Couldn't download");
                        error.printStackTrace();
                    }
                }, params);
                mRequestQueue.add(request);
            }
        });
    }

}
