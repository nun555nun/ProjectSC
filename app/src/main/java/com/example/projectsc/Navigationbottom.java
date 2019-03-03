package com.example.projectsc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class Navigationbottom extends AppCompatActivity {


    Bundle bundle;
    String binID;
    String startDate;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Intent intent = getIntent();
            binID = intent.getStringExtra("binID");
            startDate = intent.getStringExtra("startDate");
            bundle = new Bundle();
            bundle.putString("binID", binID);
            String binName = intent.getStringExtra("binName");
            setTitle(binName);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment selectedFragment;

            Bundle b = new Bundle();
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    selectedFragment = new HomeFragment();
                    selectedFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();
                    return true;

                case R.id.navigation_history:


                    selectedFragment =  new TabHistory();

                    b.putString("binID",binID);
                    b.putString("startDate",startDate);
                    selectedFragment.setArguments(b);

                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();

                    return true;

                case R.id.navigation_notifications:

                    selectedFragment = new SettingBinFragment();

                    b.putString("binID",binID);
                    selectedFragment.setArguments(b);

                    fragmentTransaction.replace(R.id.framz, selectedFragment).commit();

                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigationbottom);

        Intent intent = getIntent();
        binID = intent.getStringExtra("binID");
        bundle = new Bundle();
        bundle.putString("binID", binID);
        String binName = intent.getStringExtra("binName");
        setTitle(binName);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment selectedFragment = new HomeFragment();

        selectedFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.framz, selectedFragment).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}