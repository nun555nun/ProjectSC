package com.example.projectsc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class login extends AppCompatActivity {

    public static final String NODE_USER = "users";
    public static final String NODE_fcm = "fcm-token";

    private EditText editTextemail;
    private EditText editTextpass;
    private Button buttonlogin;
    public ProgressBar progressBar;
    public FirebaseAuth auth;
    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextemail = findViewById(R.id.eemail);
        editTextpass = findViewById(R.id.epass);
        progressBar = findViewById(R.id.progressBar);
        buttonlogin = findViewById(R.id.buttonlogin);

        auth = FirebaseAuth.getInstance();
        token = FirebaseInstanceId.getInstance().getToken();
        if (auth.getCurrentUser() != null) {
            //Toast.makeText(getApplicationContext(), "OK, you already logged in!", Toast.LENGTH_SHORT).show();
            saveToken(token);

            Intent intent = new Intent(login.this, Main.class);
            startActivity(intent);
            finish();
        }
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextemail.getText().toString();
                final String password = editTextpass.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
//                    No Space
                    Toast.makeText(login.this, "โปรดกรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        if (password.length() < 6) {
                                            editTextpass.setError("ตัวอักษรต้องอย่างน้อย6ตัว");
                                        } else {
                                            Toast.makeText(login.this, "ไม่มี e-mail ในระบบ โปรดสมัคร", Toast.LENGTH_LONG).show();
                                        }
                                    } else {

                                        saveToken(token);

                                        Intent intent = new Intent(login.this, Main.class);
                                        startActivity(intent);


                                        finish();
                                    }
                                }
                            });
                }

            }
        });
        TextView t = findViewById(R.id.textView8);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);

            }
        });


    }

    private void saveToken(String token) {

        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference(NODE_fcm + "/bin1");
        dbUser.child(token).child("token").setValue(token);
        dbUser = FirebaseDatabase.getInstance().getReference(NODE_USER+"/"+auth.getUid());
        dbUser.child("token").setValue(token);
    }
}
