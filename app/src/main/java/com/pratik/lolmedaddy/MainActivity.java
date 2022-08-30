package com.pratik.lolmedaddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://meme-api.herokuapp.com/gimme/ComedyCemetery";
    private static final String TAG = "MainActivity";
    static String title;
    static URL memeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting the layout components
        ImageView meme_image = (ImageView) findViewById(R.id.meme_image);
        TextView meme_title = (TextView) findViewById(R.id.meme_title);
        Button next = (Button) findViewById(R.id.btn_next);
        Button share = (Button) findViewById(R.id.btn_share);


        // Getting the json for meme from the api
        try {
            URL getUrl = new URL(url);
        } catch (MalformedURLException e) {
            Log.i(TAG, "onCreate: Some Error Occurred!! Malformed URL ");
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "onResponse: Request Successfully Fetched!!");
                            title = response.getString("title");
                            memeUrl = new URL(response.getString("url"));
                            Log.d(TAG, "onResponse: URL for the image is: " + memeUrl);
                            Log.d(TAG, "onResponse: Title for the image is: " + title);

                            Picasso.get().load(String.valueOf(memeUrl)).into(meme_image);
                            meme_title.setText(title);



                        } catch (JSONException e) {
                            Log.i(TAG, "onResponse: Exception Occurred");

                        } catch (MalformedURLException e) {
                            Log.i(TAG, "onResponse: Some Error Occurred!! Malformed URL ");

                        }
                    }
                },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Something Went Wrong!!!");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue.cancelAll("a");
                requestQueue.stop();
                requestQueue.start();
                requestQueue.add(jsonObjectRequest);
            }
        });


        // Share Button
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) meme_image.getDrawable());
                Bitmap bitmap = bitmapDrawable .getBitmap();
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);

                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, null));
            }
        });

    }
}