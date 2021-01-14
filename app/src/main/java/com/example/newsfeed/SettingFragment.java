package com.example.newsfeed;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class SettingFragment extends Fragment {
    public SettingFragment(){}
    Spinner spinner;
    Button getNews;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_setting,container,false);
        spinner = root.findViewById(R.id.spinner);
        getNews = root.findViewById(R.id.button);
        getNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = spinner.getSelectedItemPosition();
                String code = getResources().getStringArray(R.array.supportedCountries)[index];
                NewsFragment fragment = new NewsFragment ();
                Bundle args = new Bundle();
                args.putString("code", code);
                fragment.setArguments(args);
                ViewPager viewPager = getActivity().findViewById(R.id.viewpager);
                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
                viewPager.setCurrentItem(0);
            }
        });
        populateSpinner();
        return root;
    }

    private void populateSpinner(){
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.supportedCountriesNames));
        spinner.setAdapter(arrayAdapter);
    }

}
