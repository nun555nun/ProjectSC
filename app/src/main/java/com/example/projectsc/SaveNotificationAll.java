package com.example.projectsc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SaveNotificationAll extends AppCompatActivity {
    DatabaseReference dbRef;
    String binN;
    String[] type;
    FirebaseAuth auth;
    String token;

    public ProgressDialog progressDialog;
    public String lastSeen;
    private int day, month, year;
    private Calendar mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_notification_all);
        setTitle("กำลังโหลดข้อมูล");

        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);

        type = getResources().getStringArray(R.array.notitype2);
        token = FirebaseInstanceId.getInstance().getToken();
        auth = FirebaseAuth.getInstance();
        getLassSeen();
        progressDialog = new ProgressDialog(SaveNotificationAll.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        findUserBinNotification();
    }

    private void removeLogNotification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
            dbRef.removeValue();
        }
    }

    private void findLogNotification(final String bin) {
        getbinName(bin);
        for (int i = 1; i <= 9; i++) {

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("notification/" + bin + "/" + String.valueOf(i));

            final int finalI = i;
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);

                        if (logNotification.getDate().equals(day + "/" + (month + 1) + "/" + (year + 543))) {
                            Map binId = new HashMap();
                            binId.put("binName", binN);
                            binId.put("binId", bin);
                            binId.put("date", logNotification.getDate());
                            binId.put("time", logNotification.getTime());
                            binId.put("type", type[finalI - 1]);
                            saveLogNotification(binId);
                        }

                    }
                    //dbRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        finish();
        progressDialog.dismiss();
    }

    private void getbinName(String bin) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + bin + "/" + "binName");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binN = dataSnapshot.getValue(String.class);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveLogNotification(Map binId) {
        if (auth.getCurrentUser() != null) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
            dbRef.push().setValue(binId);
        }
    }

    private void findUserBinNotification() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                removeLogNotification();
                int count = 0;
                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));
                    findLogNotification(bin);
                    count++;
                }
                if (count == 0) {
                    finish();
                    progressDialog.dismiss();
                }
                //dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(SaveNotificationAll.this, MainActivity.class);
        intent.putExtra("check", "ok");
        intent.putExtra("lastSeen", lastSeen);
        startActivity(intent);

    }

    private void getLassSeen() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/notificationLastSeen");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lastSeen = (String) dataSnapshot.getValue();

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
