package com.example.newsfeed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class NewsFragment extends Fragment {
    public NewsFragment(){}
    private static final String ERROR_MSG = "Google Play services are unavailable.";
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    List<Article> articles = new ArrayList<>();
    TextView edtInput;
    ListView lst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

       View root = inflater.inflate(R.layout.activity_news,container,false);
        edtInput = root.findViewById(R.id.edtInput);
        lst = root.findViewById(R.id.lst);
        Bundle args = getArguments();
        if (args!=null)
            System.out.println(getArguments().getString("code"));
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();

        int result = availability.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (!availability.isUserResolvableError(result)) {
                Toast.makeText(getActivity(), ERROR_MSG, Toast.LENGTH_LONG).show();
            }
        }
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if we have permission to access high accuracy fine location.
        int permission = ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        // If permission is granted, fetch the last location.
        if (permission == PackageManager.PERMISSION_GRANTED) {
            getNews(getLastLocation());
        } else {
            // If permission has not been granted, request permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(), "Location Permission Denied",
                        Toast.LENGTH_LONG).show();
            else
                getLastLocation();
        }
    }

    private String getLastLocation() {
        final String[] code = {""};
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            code[0] =updateTextView(location);
                        }
                    });
        }
        return code[0];
    }

    private String updateTextView(Location location) {
        String latLongString = "No location found";
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            latLongString = "Lat:" + lat + "\nLong:" + lng;
        }
       return getCountryCode(lat,lng);
    }

    private String getCountryCode(double latitude, double longitude){
        Geocoder geoCoder = new Geocoder(getActivity());
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
                    return "&locale="+countryId.toLowerCase();
                }
            }
            String err = "Sorry,Your country is not supported, here are some international news or you can select other county news from setting";
            final int LENGHT =5;
            final Toast toast = Toast.makeText(getActivity(), "Sorry,Your country is not supported," +
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
        String url ="https://api.thenewsapi.com/v1/news/top?api_token=FJXHCWcAs2TSwCMPkJvb72tfAJCynSF1dnZNnkxm" + countryCode+"&limit=2";


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
                                articles.add(article);
                            }
                            ArrayAdapter<Article> itemsAdapter =
                                    new ArrayAdapter<Article>(getActivity(), android.R.layout.simple_list_item_1,
                                            articles);
                            lst.setAdapter(itemsAdapter);
                            for (int i=0; i<articles.size();i++){
                                edtInput.setText(articles.get(i)+"\n\n");
                            }
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
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }
}
