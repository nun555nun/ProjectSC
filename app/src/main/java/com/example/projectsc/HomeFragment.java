package com.example.projectsc;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    DatabaseReference dbRef;
    String binID;
    TextView tvTemp;

    TextView tvHumid;

    TextView tvDateCount;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binID = getArguments().getString("binID");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTemp = view.findViewById(R.id.tv_temp_in);

        tvHumid = view.findViewById(R.id.tv_humid_in);

        tvDateCount = view.findViewById(R.id.tv_date_count);

        setData();

        return view;
    }
    private void setData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/"+binID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Map map = (Map) dataSnapshot.getValue();

                String temp = String.valueOf(map.get("temp"));

                String humid = String.valueOf(map.get("humid"));

                String dateCount = String.valueOf(map.get("dateCount"));


                tvTemp.setText(temp);
                tvDateCount.setText(dateCount);
                tvHumid.setText(humid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
