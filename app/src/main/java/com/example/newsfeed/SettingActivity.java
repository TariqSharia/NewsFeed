package com.example.newsfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import androidx.appcompat.app.AppCompatActivity;


public class SettingActivity extends AppCompatActivity {
    Spinner spinner;
    Button getNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        spinner = findViewById(R.id.spinner);
        getNews = findViewById(R.id.button);
        getNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = spinner.getSelectedItemPosition();
                String code = getResources().getStringArray(R.array.supportedCountries)[index];
                Intent returnIntent = new Intent(getBaseContext(),MainActivity.class);
                returnIntent.putExtra("code",code);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        populateSpinner();
    }

    private void populateSpinner(){
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.supportedCountriesNames));
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
