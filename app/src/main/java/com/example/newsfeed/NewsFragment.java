package com.example.newsfeed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class NewsFragment extends Fragment {
    public NewsFragment(){}
    private static final String ERROR_MSG = "Google Play services are unavailable.";
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    TextView edtInput;
    ListView lst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

       View root = inflater.inflate(R.layout.activity_news,container,false);

        edtInput = root.findViewById(R.id.edtInput);
        lst = root.findViewById(R.id.lst);
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
            getLastLocation();
        } else {
            // If permission has not been granted, request permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

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

    private void getLastLocation() {
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
                            updateTextView(location);
                        }
                    });
        }
    }

    private void updateTextView(Location location) {
        String latLongString = "No location found";
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            latLongString = "Lat:" + lat + "\nLong:" + lng;
        }
        getCountryCode(lat,lng);
    }

    private void getCountryCode(double latitude, double longitude){
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
                    System.out.println("found");
                    getNews("&locale="+countryId);
                    return;
                }
            }
            String err = "Sorry,Your country is not supported, here are some international news or you can select other county news from setting";
            Toast.makeText(getActivity(), err,Toast.LENGTH_LONG).show();
            getNews("");
        } catch (IOException e) {
            System.out.println("here we aren't");
            e.printStackTrace();
        }

    }
    //    public void getNews(String countryCode) {
//        // Instantiate the RequestQueue.
//
//        String url ="https://api.thenewsapi.com/v1/news/top?api_token=FJXHCWcAs2TSwCMPkJvb72tfAJCynSF1dnZNnkxm&locale=" + countryCode.toLowerCase();
//
//
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
//                (Request.Method.GET, url,
//                        null, new Response.Listener<JSONArray>() {
//
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        String cityID = "";
//                        try {
//                            JSONObject obj = response.getJSONObject(0);
//
//                            cityID = obj.getString("title");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        edtInput.setText(cityID);
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//
//                    }
//                });
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//
//    }
//
    public void getNews(String countryCode) {
        String url ="https://api.thenewsapi.com/v1/news/top?api_token=FJXHCWcAs2TSwCMPkJvb72tfAJCynSF1dnZNnkxm" + countryCode.toLowerCase()+"&limit=1";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String[] days;
                        try {
                            JSONArray array = response.getJSONArray("data");
                            days = new String[array.length()];
                            for(int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                String weatherDay = "";
                                weatherDay = "state: " + obj.getString("title") +
                                        "\n, date: " + obj.getString("description") +
                                        "\n, min: " + obj.getString("language") +
                                        ", max: " + obj.getString("source");
                                days[i] = weatherDay;
                            }
//                            ArrayAdapter<String> itemsAdapter =
//                                    new ArrayAdapter<String>(MainActivity.this, android.R.layout.activity_list_item,
//                                            days);
//                            lst.setAdapter(itemsAdapter);
                            for (int i=0; i<days.length;i++){
                                edtInput.setText(days[i]+"\n\n");
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
