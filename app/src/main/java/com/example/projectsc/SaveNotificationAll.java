package com.example.projectsc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SaveNotificationAll extends AppCompatActivity {
    DatabaseReference dbRef;
    String binN;
    String[] type;
    FirebaseAuth auth;
    String token;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_notification_all);
        setTitle("กำลังโหลดข้อมูล");
        type = getResources().getStringArray(R.array.notitype2);
        token = FirebaseInstanceId.getInstance().getToken();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SaveNotificationAll.this);
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        findUserBinNotification();
    }
    private void removeLogNotification() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
        dbRef.removeValue();
    }

    private void findLogNotification(final String bin) {
        getbinName(bin);
        for (int i = 1; i <= 8; i++) {

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("notification/" + bin + "/" + String.valueOf(i));

            final int finalI = i;
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);

                        Map binId = new HashMap();
                        binId.put("binName", binN);
                        binId.put("binId", bin);
                        binId.put("date", logNotification.getDate());
                        binId.put("time", logNotification.getTime());
                        binId.put("type", type[finalI - 1]);

                        saveLogNotification(binId);

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

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));
                    findLogNotification(bin);
                }

                dbRef.removeEventListener(this);
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
        intent.putExtra("check","ok");
        startActivity(intent);

    }
}
