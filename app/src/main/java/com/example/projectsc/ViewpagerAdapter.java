package com.example.projectsc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ViewpagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentListTitle = new ArrayList<>();
    private String binID ="";

    public ViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // return fragmentListTitle.get(position);
        switch (position) {
            case 0:
                return "ในถัง";

            case 1:
                return "นอกถัง";

            case 2:
                return "การแจ้งเตือน";

        }
        return null;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle b;
        switch (i) {
            case 0:
                fragment = new HistoryFragment();
                b = new Bundle();
                Log.v("zxc","0     "+binID);
                b.putString("binID", binID);
                b.putString("logDHT","logDHTIn");
                fragment.setArguments(b);
                break;
            case 1:
                fragment = new HistoryFragment();
                b = new Bundle();
                Log.v("zxc","1    "+binID);
                b.putString("binID", binID);
                b.putString("logDHT","logDHTOut");
                fragment.setArguments(b);
                break;
            case 2:
                fragment = new HistoryFragment();
                b = new Bundle();
                b.putString("binID",binID);
                fragment.setArguments(b);
                break;
        }
        return fragment;
        //return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        //return fragmentListTitle.size();
        return 3;
    }
    public void AddBinId(String bin) {
        binID=bin;
    }

    public void AddFragment(Fragment fragment, String title) {
        fragmentListTitle.add(title);
        fragmentList.add(fragment);
        // binID.add(bin);

    }
}
