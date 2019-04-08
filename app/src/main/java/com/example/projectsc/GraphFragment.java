package com.example.projectsc;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends Fragment {

    private String startDate;
    private String binID;
    ConstraintLayout clDate;
    ConstraintLayout clDateToDate;
    ConstraintLayout clAll;
    ArrayList<String> date;

    public GraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        binID = getArguments().getString("binID");
        startDate = getArguments().getString("startDate");
        Log.d("asdf", startDate + "   <------------------");
        TextView t = view.findViewById(R.id.tv_test);
        t.setText(binID + " " + startDate);

        clDate = view.findViewById(R.id.cl_date);
        clDateToDate = view.findViewById(R.id.cl_date_to_date);
        clAll = view.findViewById(R.id.cl_all);
        getDate();

        final Spinner typeShowSpinner = view.findViewById(R.id.spinner_type_show);
        String[] Typeshow = getResources().getStringArray(R.array.graphtypeshow);
        ArrayAdapter<String> adapterTypeShow = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, Typeshow);
        typeShowSpinner.setAdapter(adapterTypeShow);

        Spinner graphSpinner = view.findViewById(R.id.graph_spinner);
        String[] graphType = getResources().getStringArray(R.array.graphtype);
        ArrayAdapter<String> adapterGraph = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, graphType);
        graphSpinner.setAdapter(adapterGraph);

        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    clDate.setVisibility(View.GONE);
                    clDateToDate.setVisibility(View.GONE);
                    clAll.setVisibility(View.GONE);
                    typeShowSpinner.setVisibility(View.GONE);
                } else if (position == 1) {
                    clDate.setVisibility(View.VISIBLE);
                    clDateToDate.setVisibility(View.GONE);
                    clAll.setVisibility(View.GONE);
                    typeShowSpinner.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    clDate.setVisibility(View.GONE);
                    clDateToDate.setVisibility(View.VISIBLE);
                    clAll.setVisibility(View.GONE);
                    typeShowSpinner.setVisibility(View.VISIBLE);
                } else {
                    clDate.setVisibility(View.GONE);
                    clDateToDate.setVisibility(View.GONE);
                    clAll.setVisibility(View.VISIBLE);
                    typeShowSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner daySpinner = view.findViewById(R.id.spinner_day);
        ArrayAdapter<String> adapterday = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);
        daySpinner.setAdapter(adapterday);

        final Spinner dateSpinner = view.findViewById(R.id.spinner_date);
        ArrayAdapter<String> adapterdate = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, date);
        dateSpinner.setAdapter(adapterdate);

        final Spinner toDateSpinner = view.findViewById(R.id.spinner_to_date);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<String> toDate = new ArrayList();
                if (position == 0) {
                    toDate.add("-");
                } else {
                    for (int i = position + 1; i < date.size(); i++) {
                        toDate.add(date.get(i));
                    }
                    if (toDate.size() == 0) {
                        toDate.add("-");
                    }
                }

                ArrayAdapter<String> adaptertodate = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, toDate);
                toDateSpinner.setAdapter(adaptertodate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button buttonDay = view.findViewById(R.id.button_day);
        buttonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (daySpinner.getSelectedItem().toString().equals("-")) {
                    Toasty.error(getContext(), "กรุณาเลือกวัน", Toasty.LENGTH_SHORT).show();
                } else {
                    if (typeShowSpinner.getSelectedItemPosition() == 0) {
                        Toasty.info(getContext(), "แสดงกราฟวันที่ " + daySpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();

                    } else if (typeShowSpinner.getSelectedItemPosition() == 1) {
                        Toasty.info(getContext(), "แสดงกราฟอุณหภูมิวันที่ " + daySpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();
                    } else {
                        Toasty.info(getContext(), "แสดงกราฟความชื้นวันที่ " + daySpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(getContext(), logDHTGraph.class);
                    i.putExtra("date", daySpinner.getSelectedItem().toString());
                    i.putExtra("todate", "-");
                    i.putExtra("binID", binID);
                    i.putExtra("type", typeShowSpinner.getSelectedItemPosition());
                    startActivity(i);
                }

            }
        });
        Button buttonDate = view.findViewById(R.id.button_date_to_date);
        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateSpinner.getSelectedItem().toString().equals("-")) {
                    Toasty.error(getContext(), "กรุณาเลือกวัน", Toasty.LENGTH_SHORT).show();
                } else {

                    String toD = " ถึง " + toDateSpinner.getSelectedItem().toString();
                    if (typeShowSpinner.getSelectedItemPosition() == 0) {
                        if (toDateSpinner.getSelectedItem().toString().equals("-")) {
                            Toasty.info(getContext(), "แสดงกราฟวันที่ " + dateSpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();
                        } else {
                            Toasty.info(getContext(), "แสดงกราฟวันที่ " + dateSpinner.getSelectedItem().toString() + toD, Toasty.LENGTH_SHORT).show();
                        }

                    } else if (typeShowSpinner.getSelectedItemPosition() == 1) {
                        if (toDateSpinner.getSelectedItem().toString().equals("-")) {
                            Toasty.info(getContext(), "แสดงกราฟอุณหภูมิวันที่ " + dateSpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();
                        } else {
                            Toasty.info(getContext(), "แสดงกราฟอุณหภูมิวันที่ " + dateSpinner.getSelectedItem().toString() + toD, Toasty.LENGTH_SHORT).show();
                        }

                    } else {
                        if (toDateSpinner.getSelectedItem().toString().equals("-")) {
                            Toasty.info(getContext(), "แสดงกราฟความชื้นวันที่ " + dateSpinner.getSelectedItem().toString(), Toasty.LENGTH_SHORT).show();
                        } else {
                            Toasty.info(getContext(), "แสดงกราฟความชื้นวันที่ " + dateSpinner.getSelectedItem().toString() + toD, Toasty.LENGTH_SHORT).show();
                        }
                    }
                    Intent i = new Intent(getContext(), logDHTGraph.class);
                    i.putExtra("date", dateSpinner.getSelectedItem().toString());
                    i.putExtra("todate", toDateSpinner.getSelectedItem().toString());
                    i.putExtra("binID", binID);
                    i.putExtra("type", typeShowSpinner.getSelectedItemPosition());
                    startActivity(i);
                }

            }
        });

        Button buttonAll = view.findViewById(R.id.button_all);
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), logDHTGraph.class);
                i.putExtra("date", "-");
                i.putExtra("todate", "-");
                i.putExtra("binID", binID);
                i.putExtra("type", typeShowSpinner.getSelectedItemPosition());
                startActivity(i);
            }
        });
        return view;
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
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
