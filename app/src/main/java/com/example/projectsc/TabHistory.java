package com.example.projectsc;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabHistory extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public TabHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_history, container, false);
        tabLayout = view.findViewById(R.id.tablaout_history);
        viewPager = view.findViewById(R.id.viewpager_history);

        ViewpagerAdapter adapter = new ViewpagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new HistoryFragment(), "ภายในถัง");
        adapter.AddFragment(new HistoryFragment(), "ภายนอกถัง");
        adapter.AddFragment(new HistoryFragment(), "การแจ้งเตือน");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        return view;
    }

}