package com.example.projectsc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogAllbinNotification> logDHTList;
    public FirebaseAuth auth;
    String lastSeen;
    private TextView tv_noti;
    private int day, month, year;
    private int hours, minute, second;
    private Calendar mDate;
    private String newLastSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mai);


        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);
        hours = mDate.get(Calendar.HOUR_OF_DAY);
        minute = mDate.get(Calendar.MINUTE);
        second = mDate.get(Calendar.SECOND);

        Intent i = getIntent();
        setTitle(R.string.notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
        String check = i.getStringExtra("check");
        listViewLogDHT = findViewById(R.id.list_all_noti);
        tv_noti = findViewById(R.id.tv_noti);
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        lastSeen = i.getStringExtra("lastSeen");

        Log.d("asdf", lastSeen);
        if (check.equals("ok")) {
            setAdaptor();
        }

    }

    public void setAdaptor() {
        // dbRef.orderByChild("time").limitToLast(10).addValueEventListener(new ValueEventListener() {
        dbRef.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(MainActivity.this, R.anim.layout_fall_down);
                logDHTList.clear();
                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogAllbinNotification logDHT = logDHTSnapshot.getValue(LogAllbinNotification.class);
                    logDHTList.add(logDHT);

                }
                if (logDHTList.size() > 0 && MainActivity.this != null) {
                    sortTime();
                    //Collections.reverse(logDHTList);
                    sortDate();
                    Collections.reverse(logDHTList);
                    newLastSeen= logDHTList.get(0).getDate()+" "+logDHTList.get(0).getTime();

                    if (logDHTList.size() >= 20) {
                        logDHTList = logDHTList.subList(0, 20);
                    }

                    progressDialog.cancel();
                    LogAllbinNotificationList adapter = new LogAllbinNotificationList(MainActivity.this, logDHTList, lastSeen);
                    listViewLogDHT.setAdapter(adapter);

                    listViewLogDHT.setLayoutAnimation(controller);
                    listViewLogDHT.scheduleLayoutAnimation();
                    setLastSeen();
                } else if (logDHTList.size() == 0 && MainActivity.this != null) {
                    logDHTList.clear();
                    tv_noti.setVisibility(View.VISIBLE);
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sortDate() {

        Collections.sort(logDHTList, new Comparator<LogAllbinNotification>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(LogAllbinNotification o1, LogAllbinNotification o2) {
                try {
                    return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private void sortTime() {

        Collections.sort(logDHTList, new Comparator<LogAllbinNotification>() {
            DateFormat f = new SimpleDateFormat("HH:mm:ss");

            @Override
            public int compare(LogAllbinNotification o1, LogAllbinNotification o2) {
                try {
                    return f.parse(o1.getTime()).compareTo(f.parse(o2.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // setLastSeen();
        // Toast.makeText(MainActivity.this,day+"/"+(month+1)+"/"+(year+543)+" "+hours+":"+minute+":"+second,Toast.LENGTH_SHORT).show();
    }

    private void setLastSeen() {

        if(newLastSeen!=null){
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/notificationLastSeen");
            dbRef.setValue(newLastSeen);
        }
    /*
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/notificationLastSeen");
        dbRef.setValue(day + "/" + (month + 1) + "/" + (year + 543) + " " + hours + ":" + minute + ":" + second);*/
    }


}
