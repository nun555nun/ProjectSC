package com.example.projectsc;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryNotificationFragment extends Fragment {
    String startDate;
    String binID;

    private Spinner dateSpinner;
    private Spinner typeSpinner;

    ArrayList<String> type;
    ArrayList<String> date;

    public HistoryNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binID = getArguments().getString("binID");
        startDate = getArguments().getString("startDate");
        View view = inflater.inflate(R.layout.fragment_history_notification, container, false);

        TextView tv = view.findViewById(R.id.bin);
        tv.setText(binID + "     " + startDate);

        dateSpinner = view.findViewById(R.id.dateN_spinner);
        typeSpinner = view.findViewById(R.id.type_spinner);

        String[] type = getResources().getStringArray(R.array.notitype);
        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, type);
        typeSpinner.setAdapter(adapterSort);

        getDate();
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);
        dateSpinner.setAdapter(adapterDate);

        Button searchButton = view.findViewById(R.id.search_log_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "ค้นหา การแจ้งเตือน '" + typeSpinner.getSelectedItem().toString() + "' วันที่ " + dateSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void getDate() {
        date = new ArrayList<>();
        date.add("-");

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();
                    binPart = binPart.replace("_", "/");
                    date.add(binPart);

                    sortDate();
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sortDate() {

        Collections.sort(date, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
