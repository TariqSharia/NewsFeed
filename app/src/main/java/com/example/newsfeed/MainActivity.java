package com.example.newsfeed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private static final String ERROR_MSG = "Google Play services are unavailable.";
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    List<Article> articles = new ArrayList<>();
    boolean setting = false;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.news_recycler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            setting = true;
            if(resultCode == Activity.RESULT_OK){
                String code=data.getStringExtra("code");
                getNews(code);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                getNews("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivityForResult(new Intent(this,SettingActivity.class),1);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!setting){
            GoogleApiAvailability availability = GoogleApiAvailability.getInstance();

            int result = availability.isGooglePlayServicesAvailable(this);
            if (result != ConnectionResult.SUCCESS) {
                if (!availability.isUserResolvableError(result)) {
                    Toast.makeText(this, ERROR_MSG, Toast.LENGTH_LONG).show();
                }
            }
            int permission = ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED) {
                getNews(getLastLocation());
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Location Permission Denied",
                        Toast.LENGTH_LONG).show();
            else
                getLastLocation();
        }
    }

    private String getLastLocation() {
        final String[] code = {""};
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            code[0] =getLongLat(location);
                        }
                    });
        }
        return code[0];
    }

    private String getLongLat(Location location) {
        double lat =0;
        double lng =0;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        return getCountryCode(lat,lng);
    }

    private String getCountryCode(double latitude, double longitude){
        Geocoder geoCoder = new Geocoder(this);
        List<Address> addresses = null;
        String countryId = "";
        String[] countryCodes = getResources().getStringArray(R.array.supportedCountries);
        try {
            System.out.println(latitude+"     "+longitude );
            addresses = geoCoder.getFromLocation( latitude,longitude, 1);
            if (addresses.size() > 0) {
                countryId= addresses.get(0).getCountryCode();
            }
            for (int i=0;i<countryCodes.length;i++){
                if (countryId!=null&&countryId.equals(countryCodes[i])){
                    return countryId.toLowerCase();
                }
            }
            String err = "Sorry,Your country is not supported, here are some international news or you can select other county news from setting";
            final Toast toast = Toast.makeText(this, "Sorry,Your country is not supported," +
                    " here are some international news or you can select other county news from setting", Toast.LENGTH_LONG);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 3000);

        } catch (IOException e) {
            System.out.println("here we aren't");
            e.printStackTrace();
        }
        return "";
    }

    public void getNews(String countryCode) {
        final String code =countryCode;
        if (!countryCode.equals("")){
            countryCode = "&locale="+countryCode;
        }else {
            countryCode = "&language=en";
        }
        String url ="https://api.thenewsapi.com/v1/news/top?api_token=FJXHCWcAs2TSwCMPkJvb72tfAJCynSF1dnZNnkxm" + countryCode;
        System.out.println(url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray array = response.getJSONArray("data");
                            for(int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                Article article = new Article();
                                article.setArticleID(obj.getString("uuid"));
                                article.setTitle(obj.getString("title"));
                                article.setDescription(obj.getString("description"));
                                if (obj.has("keywords")){
                                    article.setKeywords(obj.getString("keywords"));
                                }else {
                                    article.setKeywords("no Key words");
                                }
                                if (obj.has("snippet")) {
                                    article.setSnippet(obj.getString("snippet"));
                                }
                                article.setUrl(obj.getString("url"));
                                if (obj.has("image_url")){
                                    article.setImage_url(obj.getString("image_url"));
                                }else {
                                    article.setImage_url("no image");
                                }
                                article.setLanguage(obj.getString("language"));
                                article.setPublishDate(obj.getString("published_at"));
                                article.setSource(obj.getString("source"));
                                article.setCountry(obj.getString("locale"));
                                ArrayList<String> list = new ArrayList<>();
                                JSONArray array1 = new JSONArray();
                                array1 = obj.getJSONArray("categories");
                                if (array1!= null){
                                    for (int j=0;j<array1.length();j++){
                                        list.add(String.valueOf(array1.get(j)));
                                    }
                                }
                                article.setCategories(list);
                                boolean add = true;
                                if (articles.size()!=0){
                                    for (int j=0;j<articles.size();j++){
                                        if (articles.get(j).getArticleID().equals(article.getArticleID()))
                                            add =false;
                                    }
                                }
                                if (add)
                                    articles.add(article);
                            }
                            ContentAdapter itemsAdapter;
                            if (url.contains("&locale")) {
                                System.out.println(code+ "    "+articles.size());
                                List<Article> newsList = new ArrayList<>();
                                for (int i = 0; i < articles.size(); i++) {
                                    System.out.println(articles.get(i).getCountry());
                                    if (articles.get(i).getCountry().equals(code)) {
                                        newsList.add(articles.get(i));
                                    }
                                }

                                itemsAdapter = new ContentAdapter(newsList);
                            }else{
                                itemsAdapter = new ContentAdapter(articles);
                            }
                            recyclerView.setAdapter(itemsAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}