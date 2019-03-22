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


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryNotificationFragment extends Fragment {
    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogNotification> logNotiList;
    TextView tv;

    String startDate;
    String binID;

    private Spinner dateSpinner;
    private Spinner typeSpinner;

    //String[] type;
    ArrayList<String> date;

    public HistoryNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        logNotiList = new ArrayList<>();
        binID = getArguments().getString("binID");
        startDate = getArguments().getString("startDate");
        View view = inflater.inflate(R.layout.fragment_history_notification, container, false);

        tv = view.findViewById(R.id.bin);
        tv.setText("ไม่พบข้อมูลการแจ้งเตือน");
        tv.setVisibility(View.GONE);
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
                if (typeSpinner.getSelectedItem().toString().equals("-") && dateSpinner.getSelectedItem().toString().equals("-")) {
                    /*setAdaptorAll();*/
                    Toast.makeText(getContext(),"โปรดเลือกรูปแบบการแจ้งเตือน",Toast.LENGTH_SHORT).show();
                } else if (!typeSpinner.getSelectedItem().toString().equals("-") && dateSpinner.getSelectedItem().toString().equals("-")) {
                    setAdaptorType();
                } else if (typeSpinner.getSelectedItem().toString().equals("-") && !dateSpinner.getSelectedItem().toString().equals("-")) {
                     /*String datePart = dateSpinner.getSelectedItem().toString();
                    setAdaptorDate(datePart);*/
                    Toast.makeText(getContext(),"โปรดเลือกรูปแบบการแจ้งเตือน",Toast.LENGTH_SHORT).show();
                } else if (!typeSpinner.getSelectedItem().toString().equals("-") && !dateSpinner.getSelectedItem().toString().equals("-")) {
                    setAdaptor();
                }

                if(dateSpinner.getSelectedItem().toString().equals("-")){
                    Toast.makeText(getContext(), "ค้นหา การแจ้งเตือน '" + typeSpinner.getSelectedItem().toString() , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "ค้นหา การแจ้งเตือน '" + typeSpinner.getSelectedItem().toString() + "' วันที่ " + dateSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });


        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);

        return view;
    }

    private void setAdaptor() {
        logNotiList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;
        final String datePart = dateSpinner.getSelectedItem().toString();

        String timePart = String.valueOf(typeSpinner.getSelectedItemPosition());

        if (timePart.equals("0")) {
            timePart = "5";
        } else if (timePart.equals("1")) {
            timePart = "3";
        } else if (timePart.equals("2")) {
            timePart = "4";
        } else if (timePart.equals("3")) {
            timePart = "6";
        } else if (timePart.equals("4")) {
            timePart = "1";
        } else if (timePart.equals("5")) {
            timePart = "2";
        } else if (timePart.equals("6")) {
            timePart = "7";
        } else if (timePart.equals("7")) {
            timePart = "8";
        }

        dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID + "/" + timePart);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logNotiList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);
                        logNotification.setType(typeSpinner.getSelectedItem().toString());
                        if (datePart.equals(logNotification.getDate())) {
                            logNotiList.add(logNotification);
                        }
                    }
                    Collections.reverse(logNotiList);
                    if (logNotiList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.GONE);
                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    if (logNotiList.size() == 0 && getContext() != null) {
                        logNotiList.clear();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.VISIBLE);
                        listViewLogDHT.setAdapter(adapter);
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

    private void setAdaptorDate(String datePart) {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;

        logNotiList.clear();
        for (int i = 1; i <= 8; i++) {
            getDataDate(String.valueOf(i),datePart);
        }
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);

        if (logNotiList.size() > 0 && getContext() != null) {
            progressDialog.dismiss();
            LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
            tv.setVisibility(View.GONE);
            listViewLogDHT.setAdapter(adapter);
            listViewLogDHT.setLayoutAnimation(controller);
            listViewLogDHT.scheduleLayoutAnimation();
        }
        else if (logNotiList.size() == 0 && getContext() != null) {

            logNotiList.clear();
            LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
            tv.setVisibility(View.VISIBLE);
            listViewLogDHT.setAdapter(adapter);
        }
        progressDialog.dismiss();

    }

    private void getDataDate(final String typePart, final String datePart) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID + "/" + typePart);
        final String[] timePart = {typePart};

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);
                    String[] type = getResources().getStringArray(R.array.notitype);
                    if (timePart[0].equals("5")) {
                        timePart[0] = "1";
                    } else if (timePart[0].equals("3")) {
                        timePart[0] = "2";
                    } else if (timePart[0].equals("4")) {
                        timePart[0] = "3";
                    } else if (timePart[0].equals("6")) {
                        timePart[0] = "4";
                    } else if (timePart[0].equals("1")) {
                        timePart[0] = "5";
                    } else if (timePart[0].equals("2")) {
                        timePart[0] = "6";
                    }
                    logNotification.setType(type[Integer.parseInt(timePart[0])]);
                    if(datePart.equals(logNotification.getDate())){
                        logNotiList.add(logNotification);
                    }

                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setAdaptorAll() {

        logNotiList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;
        final String datePart = dateSpinner.getSelectedItem().toString();

        String timePart = String.valueOf(typeSpinner.getSelectedItemPosition());

        if (timePart.equals("1")) {
            timePart = "5";
        } else if (timePart.equals("2")) {
            timePart = "3";
        } else if (timePart.equals("3")) {
            timePart = "4";
        } else if (timePart.equals("4")) {
            timePart = "6";
        } else if (timePart.equals("5")) {
            timePart = "1";
        } else if (timePart.equals("6")) {
            timePart = "2";
        }

        dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID + "/" + timePart);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logNotiList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);
                        logNotification.setType(typeSpinner.getSelectedItem().toString());
                        if (datePart.equals(logNotification.getDate())) {
                            logNotiList.add(logNotification);
                        }
                    }
                    Collections.reverse(logNotiList);
                    if (logNotiList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.GONE);
                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    if (logNotiList.size() == 0 && getContext() != null) {
                        logNotiList.clear();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.VISIBLE);
                        listViewLogDHT.setAdapter(adapter);
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

    public void setAdaptorType() {
        logNotiList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        final DatabaseReference dbRef;
        String datePart = dateSpinner.getSelectedItem().toString();

        String timePart = String.valueOf(typeSpinner.getSelectedItemPosition());

        if (timePart.equals("0")) {
            timePart = "5";
        } else if (timePart.equals("1")) {
            timePart = "3";
        } else if (timePart.equals("2")) {
            timePart = "4";
        } else if (timePart.equals("3")) {
            timePart = "6";
        } else if (timePart.equals("4")) {
            timePart = "1";
        } else if (timePart.equals("5")) {
            timePart = "2";
        } else if (timePart.equals("6")) {
            timePart = "7";
        } else if (timePart.equals("7")) {
            timePart = "8";
        }

        dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID + "/" + timePart);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    dbRef.removeEventListener(this);
                } else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logNotiList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);
                        logNotification.setType(typeSpinner.getSelectedItem().toString());
                        logNotiList.add(logNotification);
                    }
                    Collections.reverse(logNotiList);
                    if (logNotiList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.GONE);
                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    if (logNotiList.size() == 0 && getContext() != null) {
                        logNotiList.clear();
                        LogNotificationList adapter = new LogNotificationList(getContext(), logNotiList);
                        tv.setVisibility(View.VISIBLE);
                        listViewLogDHT.setAdapter(adapter);
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

}