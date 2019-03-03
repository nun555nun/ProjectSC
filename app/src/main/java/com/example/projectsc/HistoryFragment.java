package com.example.projectsc;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogDHT> logDHTList;

    String logDHTType;
    private Spinner dateSpinner;
    private Spinner timeSpinner;

    ArrayList<String> time;
    ArrayList<String> date;
    String startDate;
    String binID;
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binID = getArguments().getString("binID");
        startDate = getArguments().getString("startDate");
        Log.d("asdf", startDate + "   <------------------");
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        dateSpinner = view.findViewById(R.id.date_spinner);
        timeSpinner = view.findViewById(R.id.time_spinner);
        getDate();

        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);

        dateSpinner.setAdapter(adapterDate);
        //getTime();
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getTime();

                ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, time);
                timeSpinner.setAdapter(adapterTime);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button searchButton = view.findViewById(R.id.search_log_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdaptor();
                Toast.makeText(getContext(), "ค้นหาวันที่ " + dateSpinner.getSelectedItem().toString() + " ช่วงเวลา " + timeSpinner.getSelectedItem().toString()+" โมง", Toast.LENGTH_SHORT).show();
            }
        });


        logDHTType = getArguments().getString("logDHT");

        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/" + logDHTType);

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);

        //progressDialog.show();
        //setAdaptor();
        return view;
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

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setAdaptor() {
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;
        String datePart = dateSpinner.getSelectedItem().toString();
        datePart = datePart.replace("/", "_");

        String timePart = timeSpinner.getSelectedItem().toString();
        if(timePart.equals("-")){
            dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date/"+datePart+ "/" + logDHTType);
        }
        else {
            dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time/"+datePart+"/"+timePart+ "/" + logDHTType);
        }

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logDHTList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                        logDHTList.add(logDHT);
                    }
                    if (logDHTList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogDHTList adapter = new LogDHTList(getContext(), logDHTList);

                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    progressDialog.dismiss();
                }

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTime() {
        time = new ArrayList<>();
        time.add("-");
        String datePart = dateSpinner.getSelectedItem().toString();
        datePart = datePart.replace("/", "_");
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time/" + datePart);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();

                    time.add(binPart);


                    Log.d("asdf", binPart);
                }
                Log.d("asdf", "_________________");

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDate() {
        date = new ArrayList<>();
        date.add(startDate);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();
                    binPart = binPart.replace("_", "/");
                    if (!binPart.equals(startDate)) {
                        date.add(binPart);
                    }
                    sortDate();
                    Log.d("asdf", binPart);
                }

                Log.d("asdf", "_________________");
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}

