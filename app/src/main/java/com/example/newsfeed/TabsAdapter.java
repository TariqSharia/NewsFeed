package com.example.newsfeed;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.news_tab);
        } else {
            return mContext.getString(R.string.setting_tab);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NewsFragment();
        } else {
            return new SettingFragment();
        }
    }
}

