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
import java.util.List;
import java.util.stream.Collectors;


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

    TextView tv_tMax;
    TextView tv_tMin;
    TextView tv_hMax;
    TextView tv_hMin;
    TextView tv_log;

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

        tv_tMax = view.findViewById(R.id.tv_tMax);
        tv_tMin = view.findViewById(R.id.tv_tMin);
        tv_hMax = view.findViewById(R.id.tv_hMax);
        tv_hMin = view.findViewById(R.id.tv_hMin);
        tv_log = view.findViewById(R.id.tv_log);
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
                if (dateSpinner.getSelectedItem().toString().equals("-")) {
                    Toast.makeText(getContext(), "ค้นหาทั้งหมด", Toast.LENGTH_SHORT).show();
                } else if(!timeSpinner.getSelectedItem().toString().equals("-")&&!dateSpinner.getSelectedItem().toString().equals("-")){
                    Toast.makeText(getContext(), "ค้นหาวันที่ " + dateSpinner.getSelectedItem().toString() + " ช่วงเวลา " + timeSpinner.getSelectedItem().toString() + " นาฬิกา", Toast.LENGTH_SHORT).show();
                }else if (!dateSpinner.getSelectedItem().toString().equals("-")){
                    Toast.makeText(getContext(), "ค้นหาวันที่ " + dateSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        logDHTType = getArguments().getString("logDHT");

        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/" + logDHTType);

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);

        return view;
    }



    public void setAdaptor() {
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;

        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    logDHTList.clear();
                    int type = 0;
                    String tempMax = "0.0";
                    String tempMin = "1000.0";
                    String humidMax = "0.0";
                    String humidMin = "1000.0";
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);

                    if(dateSpinner.getSelectedItem().toString().equals("-")&&timeSpinner.getSelectedItem().toString().equals("-")){
                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                            logDHTList.add(logDHT);
                            String temp = logDHT.getTemperature();
                            String humid = logDHT.getHumidity();
                            temp = temp.substring(0, temp.indexOf(" "));
                            humid = humid.substring(0, humid.indexOf(" "));

                            if (Double.parseDouble(tempMax) < Double.parseDouble(temp)) {
                                tempMax = temp;
                            }
                            if (Double.parseDouble(tempMin) > Double.parseDouble(temp)) {
                                tempMin = temp;
                            }
                            if (Double.parseDouble(humidMax) < Double.parseDouble(humid)) {
                                humidMax = humid;
                            }
                            if (Double.parseDouble(humidMin) > Double.parseDouble(humid)) {
                                humidMin = humid;
                            }

                        }
                        type=0;

                    }
                    else if(!dateSpinner.getSelectedItem().toString().equals("-")&&timeSpinner.getSelectedItem().toString().equals("-")){
                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                            if(logDHT.getDate().equals(dateSpinner.getSelectedItem().toString())){
                                logDHTList.add(logDHT);
                                String temp = logDHT.getTemperature();
                                String humid = logDHT.getHumidity();
                                temp = temp.substring(0, temp.indexOf(" "));
                                humid = humid.substring(0, humid.indexOf(" "));

                                if (Double.parseDouble(tempMax) < Double.parseDouble(temp)) {
                                    tempMax = temp;
                                }
                                if (Double.parseDouble(tempMin) > Double.parseDouble(temp)) {
                                    tempMin = temp;
                                }
                                if (Double.parseDouble(humidMax) < Double.parseDouble(humid)) {
                                    humidMax = humid;
                                }
                                if (Double.parseDouble(humidMin) > Double.parseDouble(humid)) {
                                    humidMin = humid;
                                }
                            }

                        }
                        type=1;
                    }else if(!dateSpinner.getSelectedItem().toString().equals("-")&&!timeSpinner.getSelectedItem().toString().equals("-")){
                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                            String timeString = logDHT.getTime();
                            String subTime = timeString.substring(0, timeString.indexOf(":"));
                            if(logDHT.getDate().equals(dateSpinner.getSelectedItem().toString())&&subTime.equals(timeSpinner.getSelectedItem().toString())){
                                logDHTList.add(logDHT);
                                String temp = logDHT.getTemperature();
                                String humid = logDHT.getHumidity();
                                temp = temp.substring(0, temp.indexOf(" "));
                                humid = humid.substring(0, humid.indexOf(" "));

                                if (Double.parseDouble(tempMax) < Double.parseDouble(temp)) {
                                    tempMax = temp;
                                }
                                if (Double.parseDouble(tempMin) > Double.parseDouble(temp)) {
                                    tempMin = temp;
                                }
                                if (Double.parseDouble(humidMax) < Double.parseDouble(humid)) {
                                    humidMax = humid;
                                }
                                if (Double.parseDouble(humidMin) > Double.parseDouble(humid)) {
                                    humidMin = humid;
                                }
                            }

                        }
                        type=2;

                    }

                    if (logDHTList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogDHTList adapter = new LogDHTList(getContext(), logDHTList,type,tempMax + " °C",tempMin + " °C",humidMax + " %",humidMin + " %");

                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();

                        tv_tMax.setText(tempMax + " °C");
                        tv_tMin.setText(tempMin + " °C");
                        tv_hMax.setText(humidMax + " %");
                        tv_hMin.setText(humidMin + " %");
                    } else if (logDHTList.size() == 0 && getContext() != null){
                        tv_log.setVisibility(View.VISIBLE);
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
        final String datePart = dateSpinner.getSelectedItem().toString();
        if(!datePart.equals("-")){
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                        if(logDHT.getDate().equals(datePart)){
                            String timeString = logDHT.getTime();
                            String subTime = timeString.substring(0, timeString.indexOf(":"));
                            if (!time.get(time.size() - 1).equals(subTime)) {
                                time.add(subTime);
                            }
                        }
                    }

                    dbRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void getDate() {
        date = new ArrayList<>();
        date.add("-");

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                    String dateString = logDHT.getDate();
                    if (!date.get(date.size() - 1).equals(dateString)) {
                        date.add(dateString);
                    }
                }
                //dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /*private void getDate() {
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


    }*/
}

