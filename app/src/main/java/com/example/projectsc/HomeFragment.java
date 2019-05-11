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

import es.dmoral.toasty.Toasty;


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
        tvStatusAir = view.findViewById(R.id.tv_air_status2);
        tvStatusWater = view.findViewById(R.id.tv_water_status3);

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
                                    Toast.makeText(getContext(), "กำลังปิดปั้มลม", Toast.LENGTH_SHORT).show();
                                    buttonAirClose.setBackgroundResource(R.drawable.button_gray);
                                    buttonAirClose.setClickable(false);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(), "กำลังปิดปั้มน้ำ", Toast.LENGTH_SHORT).show();
                                    buttonWaterClose.setBackgroundResource(R.drawable.button_gray);
                                    buttonWaterClose.setClickable(false);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
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
                                    Toasty.success(getContext(), "กำลังทำการเปิดปั๊มลม", Toast.LENGTH_SHORT).show();
                                    buttonAirOpen.setBackgroundResource(R.drawable.button_gray);
                                    buttonAirOpen.setClickable(false);

                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });
        buttonWaterOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการเปิดปั้มน้ำใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setStatusButtonOpen("statusWater");
                                    Toasty.success(getContext(), "กำลังทำการเปิดปั๊มน้ำ", Toast.LENGTH_SHORT).show();
                                    buttonWaterOpen.setBackgroundResource(R.drawable.button_gray);
                                    buttonWaterOpen.setClickable(false);
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
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
                dbRef.child("statuswork").setValue(1);
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
                dbRef.child("statuswork").setValue(1);
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
                String tempMax = String.valueOf(map.get("tempMax"));
                String tempMin = String.valueOf(map.get("tempMin"));

                String humid = String.valueOf(map.get("humid"));
                String humidMax = String.valueOf(map.get("humidMax"));
                String humidMin = String.valueOf(map.get("humidMin"));

                String time = String.valueOf(map.get("time"));
                String statusAirWork = String.valueOf(map.get("statusAirWork"));
                String statusWaterWork = String.valueOf(map.get("statusWaterWork"));

                String statusAir = String.valueOf(map.get("statusAir"));
                String statusWater = String.valueOf(map.get("statusWater"));

                String startDate = String.valueOf(map.get("startDate"));
                String endDate = String.valueOf(day + "/" + (month + 1) + "/" + (year + 543));
                int dayDiff = dateDiff(startDate, endDate);
                dbRef.child("dateCount").setValue(dayDiff);

                String dateCount = String.valueOf(map.get("dateCount"));
                if (temp.equals("null")) {
                    tvTemp.setText("-");
                } else {
                    if(!temp.equals("-")){
                        tvTemp.setText(temp);
                        tvTemp.setTextColor(Color.parseColor("#4CAF50"));
                        if (Float.parseFloat(temp.substring(0, temp.indexOf(" "))) < Float.parseFloat(tempMin.substring(0, tempMin.indexOf(" ")))) {
                            tvTemp.setTextColor(Color.parseColor("#16B4FD"));
                        }
                        if (Float.parseFloat(temp.substring(0, temp.indexOf(" "))) > Float.parseFloat(tempMax.substring(0, tempMax.indexOf(" ")))) {
                            tvTemp.setTextColor(Color.parseColor("#E91E63"));
                        }
                    }


                }
                if (dateCount.equals("null")) {
                    tvDateCount.setText("-");
                } else {
                    tvDateCount.setText(dateCount);
                }
                if (humid.equals("null")) {
                    tvHumid.setText("-");
                } else {
                    if(!humid.equals("-")){
                        tvHumid.setText(humid);
                        tvHumid.setTextColor(Color.parseColor("#4CAF50"));
                        if (Float.parseFloat(humid.substring(0, humid.indexOf(" "))) < Float.parseFloat(humidMin.substring(0, humidMin.indexOf(" ")))) {
                            tvHumid.setTextColor(Color.parseColor("#16B4FD"));
                        }
                        if (Float.parseFloat(humid.substring(0, humid.indexOf(" "))) > Float.parseFloat(humidMax.substring(0, humidMax.indexOf(" ")))) {
                            tvHumid.setTextColor(Color.parseColor("#E91E63"));
                        }
                    }
                }
                if (time.equals("null")) {
                    tvTime.setText("-");
                } else {
                    tvTime.setText(time);
                }

                if (statusAirWork.equals("1")) {
                    tvStatusAir.setText("กำลังทำงานอยู่");
                    buttonAirOpen.setClickable(false);
                    buttonAirOpen.setBackgroundResource(R.drawable.button_gray);
                    buttonAirClose.setBackgroundResource(R.drawable.button_off);
                    buttonAirClose.setClickable(true);
                    tvStatusAir.setTextColor(Color.parseColor("#97CA02"));

                } else {
                    tvStatusAir.setText("ปิดอยู่");
                    buttonAirOpen.setClickable(true);
                    buttonAirClose.setClickable(false);
                    buttonAirClose.setBackgroundResource(R.drawable.button_gray);
                    buttonAirOpen.setBackgroundResource(R.drawable.button_on);
                    tvStatusAir.setTextColor(Color.LTGRAY);
                }

                if (statusWaterWork.equals("1")) {
                    tvStatusWater.setText("กำลังทำงานอยู่");
                    buttonWaterOpen.setClickable(false);
                    buttonWaterOpen.setBackgroundResource(R.drawable.button_gray);
                    buttonWaterClose.setBackgroundResource(R.drawable.button_off);
                    buttonWaterClose.setClickable(true);
                    tvStatusWater.setTextColor(Color.parseColor("#97CA02"));
                } else {
                    tvStatusWater.setText("ปิดอยู่");
                    buttonWaterOpen.setClickable(true);
                    buttonWaterClose.setClickable(false);
                    buttonWaterClose.setBackgroundResource(R.drawable.button_gray);
                    buttonWaterOpen.setBackgroundResource(R.drawable.button_on);
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

                if(statusAir.equals("2")){
                    buttonAirClose.setClickable(false);
                    buttonAirClose.setBackgroundResource(R.drawable.button_gray);
                }else if(statusAir.equals("3")){
                    buttonAirOpen.setClickable(false);
                    buttonAirOpen.setBackgroundResource(R.drawable.button_gray);
                }else {
                    if (statusAirWork.equals("1")) {
                        tvStatusAir.setText("กำลังทำงานอยู่");
                        buttonAirOpen.setClickable(false);
                        buttonAirOpen.setBackgroundResource(R.drawable.button_gray);
                        buttonAirClose.setBackgroundResource(R.drawable.button_off);
                        buttonAirClose.setClickable(true);
                        tvStatusAir.setTextColor(Color.parseColor("#97CA02"));

                    } else {
                        tvStatusAir.setText("ปิดอยู่");
                        buttonAirOpen.setClickable(true);
                        buttonAirClose.setClickable(false);
                        buttonAirClose.setBackgroundResource(R.drawable.button_gray);
                        buttonAirOpen.setBackgroundResource(R.drawable.button_on);
                        tvStatusAir.setTextColor(Color.LTGRAY);
                    }
                }
                if(statusWater.equals("2")){
                    buttonWaterClose.setClickable(false);
                    buttonWaterClose.setBackgroundResource(R.drawable.button_gray);
                }else if(statusWater.equals("3")){
                    buttonWaterOpen.setClickable(false);
                    buttonWaterOpen.setBackgroundResource(R.drawable.button_gray);
                }else {
                    if (statusWaterWork.equals("1")) {
                        tvStatusWater.setText("กำลังทำงานอยู่");
                        buttonWaterOpen.setClickable(false);
                        buttonWaterOpen.setBackgroundResource(R.drawable.button_gray);
                        buttonWaterClose.setBackgroundResource(R.drawable.button_off);
                        buttonWaterClose.setClickable(true);
                        tvStatusWater.setTextColor(Color.parseColor("#97CA02"));
                    } else {
                        tvStatusWater.setText("ปิดอยู่");
                        buttonWaterOpen.setClickable(true);
                        buttonWaterClose.setClickable(false);
                        buttonWaterClose.setBackgroundResource(R.drawable.button_gray);
                        buttonWaterOpen.setBackgroundResource(R.drawable.button_on);
                        tvStatusWater.setTextColor(Color.LTGRAY);
                    }
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
