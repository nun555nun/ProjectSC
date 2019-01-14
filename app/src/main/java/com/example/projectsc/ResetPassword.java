package com.example.projectsc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    Button resetEmail;
    EditText emailEditText;
    Animation fromBottom,fromTop;
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("รีเซตรหัสผ่าน");

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);

        fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        cl = findViewById(R.id.CL_reset);
        emailEditText = findViewById(R.id.emailEditText);
        resetEmail = findViewById(R.id.reser_buttom);

        cl.setAnimation(fromBottom);
        emailEditText.setAnimation(fromTop);
        resetEmail.setAnimation(fromTop);

        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();

                if (email.isEmpty()) {
                    emailEditText.setError("โปรดกรอก email");
                    emailEditText.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("โปรดระบุ email ให้ถูกต้อง");
                    emailEditText.requestFocus();
                } else {
                    progressDialog = new ProgressDialog(ResetPassword.this);
                    progressDialog.setTitle("Please Wait ...");

                    new AlertDialog.Builder(ResetPassword.this)
                            .setTitle("ต้องการรีเซต password ใช่หรือไม่")
                            .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.show();
                                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ResetPassword.this, "ระบบได้ทำการส่ง email ไปที่ " + email + " โปรดตรวจสอบที่ email ของคุณ หากต้องการรีเซต password", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(ResetPassword.this, login.class));
                                            } else {
                                                Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("ไม่", null)
                            .show();
                }
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
}
