package com.example.projectsc;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Date;
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
    TextView tvTime;
    TextView tvStatusAir;
    TextView tvStatusWater;
    private int day, month, year;
    private Calendar mDate;

    TextView tvST;
    TextView tvSD;
    TextView tvLT;
    TextView tvLD;

    Button buttonAirClose;
    Button buttonWaterClose;

    Button buttonAirOpen;
    Button buttonWaterOpen;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binID = getArguments().getString("binID");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTemp = view.findViewById(R.id.tv_temp);
        tvHumid = view.findViewById(R.id.tv_humid);
        tvDateCount = view.findViewById(R.id.tv_date_count);
        tvTime = view.findViewById(R.id.tv_time);
        tvStatusAir = view.findViewById(R.id.tv_air_status);
        tvStatusWater = view.findViewById(R.id.tv_water_status);

        buttonAirClose = view.findViewById(R.id.button_air_close);
        buttonWaterClose = view.findViewById(R.id.button_water_close);
        buttonAirOpen = view.findViewById(R.id.button_air_open);
        buttonWaterOpen = view.findViewById(R.id.button_water_open);

        buttonAirClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการปิดปั้มอากาศใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setStatusButtonClose("statusAir");
                                    Toast.makeText(getContext(), "ปิดปั้มลมเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });
        buttonWaterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการปิดปั้มน้ำใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setStatusButtonClose("statusWater");
                                    Toast.makeText(getContext(), "ปิดปั้มน้ำเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });
        buttonAirOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการเปิดปั้มอากาศใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setStatusButtonOpen("statusAir");
                                    Toast.makeText(getContext(), "เปิดปั้มลมเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });
        buttonWaterOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการเปิดปั้มอากาศใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setStatusButtonOpen("statusWater");
                                    Toast.makeText(getContext(), "เปิดปั้มน้ำเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toast.makeText(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);
        setData();

        tvSD = view.findViewById(R.id.tv_sd);
        tvST = view.findViewById(R.id.tv_st);
        tvLD = view.findViewById(R.id.tv_ld);
        tvLT = view.findViewById(R.id.tv_lt);

        return view;
    }

    private void setStatusButtonOpen(final String status) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbRef.child(status).setValue(3);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setStatusButtonClose(final String status) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbRef.child(status).setValue(2);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Map map = (Map) dataSnapshot.getValue();

                String temp = String.valueOf(map.get("temp"));
                String humid = String.valueOf(map.get("humid"));

                String time = String.valueOf(map.get("time"));
                String statusAir = String.valueOf(map.get("statusAirWork"));
                String statusWater = String.valueOf(map.get("statusWaterWork"));

                String startDate = String.valueOf(map.get("startDate"));
                String endDate = String.valueOf(day + "/" + (month + 1) + "/" + (year + 543));
                int dayDiff = dateDiff(startDate, endDate);
                dbRef.child("dateCount").setValue(dayDiff);

                String dateCount = String.valueOf(map.get("dateCount"));
                if (temp.equals("null")) {
                    tvTemp.setText("-");
                } else {
                    tvTemp.setText(temp);
                }
                if (dateCount.equals("null")) {
                    tvDateCount.setText("-");
                } else {
                    tvDateCount.setText(dateCount);
                }
                if (humid.equals("null")) {
                    tvHumid.setText("-");
                } else {
                    tvHumid.setText(humid);
                }
                if (time.equals("null")) {
                    tvTime.setText("-");
                } else {
                    tvTime.setText(time);
                }

                if (statusAir.equals("1")) {
                    tvStatusAir.setText("กำลังทำงานอยู่");
                    tvStatusAir.setTextColor(Color.parseColor("#97CA02"));
                } else {
                    tvStatusAir.setText("ปิดอยู่");
                    tvStatusAir.setTextColor(Color.LTGRAY);
                }

                if (statusWater.equals("1")) {
                    tvStatusWater.setText("กำลังทำงานอยู่");
                    tvStatusWater.setTextColor(Color.parseColor("#97CA02"));
                } else {
                    tvStatusWater.setText("ปิดอยู่");
                    tvStatusWater.setTextColor(Color.LTGRAY);
                }
                String st = String.valueOf(map.get("boardTime"));
                String sd = String.valueOf(map.get("boardDate"));
                String lt = String.valueOf(map.get("loopTime"));
                String ld = String.valueOf(map.get("loopDate"));



                if (st.equals("null")) {
                    tvST.setText("-");
                } else {
                    tvST.setText(st);
                }
                if (sd.equals("null")) {
                    tvSD.setText("-");
                } else {
                    tvSD.setText(sd);
                }
                if (lt.equals("null")) {
                    tvLT.setText("-");
                } else {
                    tvLT.setText(lt);
                }
                if (ld.equals("null")) {
                    tvLD.setText("-");
                } else {
                    tvLD.setText(ld);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public int dateDiff(String startDate, String endDate) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date startdate = df.parse(startDate);
            Date enddate = df.parse(endDate);

            long diff = enddate.getTime() - startdate.getTime();

            int dayDiff = (int) (diff / (24 * 60 * 60 * 1000));

            return dayDiff;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
