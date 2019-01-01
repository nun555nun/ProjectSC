package com.example.projectsc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mai);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();

        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = findViewById(R.id.textView);
                t.setText(FirebaseInstanceId.getInstance().getToken());
                //Log.d("Token = ",""+FirebaseInstanceId.getInstance().getToken());

                dbRef = database.getReference("users").child(auth.getUid());
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map map = (Map)dataSnapshot.getValue();
                        String x = String.valueOf(map.get("email"));

                        Toast.makeText(MainActivity.this,x,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                String s =auth.getUid();
                //Toast.makeText(MainActivity.this,s+ " Logout",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, login.class);
                auth.signOut();
                startActivity(intent);
                finish();

            }
        });
    }
}
