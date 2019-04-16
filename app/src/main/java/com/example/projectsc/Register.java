package com.example.projectsc;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

import static com.example.projectsc.login.NODE_USER;

public class Register extends AppCompatActivity {
    private String usernameString, emailString, passwordString;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    EditText usernameEditText;
    EditText emailEditText;
    EditText passwordEditText;
    TextView tv_pass;

    private int day, month, year;
    private int hours, minute, second;
    private Calendar mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);
        hours = mDate.get(Calendar.HOUR_OF_DAY);
        minute = mDate.get(Calendar.MINUTE);
        second = mDate.get(Calendar.SECOND);

        createToolbar();

    }

    private void hideKeybord() {

        usernameEditText.setFocusable(false);
        usernameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                usernameEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

        emailEditText.setFocusable(false);
        emailEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                emailEditText.setFocusableInTouchMode(true);

                return false;
            }
        });

        passwordEditText.setFocusable(false);
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                passwordEditText.setFocusableInTouchMode(true);

                return false;
            }
        });
    }

    private void passwordHideShow() {
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEditText.getText().length() > 0) {
                    tv_pass.setVisibility(View.VISIBLE);
                } else {
                    tv_pass.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_pass.getText().equals("1")) {
                    tv_pass.setText("0");
                    passwordEditText.setTransformationMethod(null);
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                } else {
                    tv_pass.setText("1");
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                }
            }
        });
    }

    private void createToolbar() {

        tv_pass = findViewById(R.id.visible_pass);
        tv_pass.setVisibility(View.GONE);

        usernameEditText = findViewById(R.id.editname);
        emailEditText = findViewById(R.id.editemail);
        passwordEditText = findViewById(R.id.editpass);
        passwordHideShow();
        hideKeybord();
        Button b = findViewById(R.id.button5);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameString = usernameEditText.getText().toString().trim();
                emailString = emailEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();

                if (usernameString.isEmpty()) {
                    usernameEditText.setError("โปรดกรอก username");
                    usernameEditText.requestFocus();
                }
                if (emailString.isEmpty()) {
                    emailEditText.setError("โปรดกรอก email");
                    emailEditText.requestFocus();
                }
                if (passwordString.isEmpty()) {
                    passwordEditText.setError("โปรดกรอก password");
                    passwordEditText.requestFocus();
                }
                if (passwordString.length() < 6) {
                    passwordEditText.setError("ตัวอักษรต้องอย่างน้อย6ตัว");
                    passwordEditText.requestFocus();
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    emailEditText.setError("โปรดระบุ email ให้ถูกต้อง");
                    emailEditText.requestFocus();
                }

                if (!usernameString.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailString).matches() && passwordString.length() >= 6) {
                    saveValueToFirebase();
                }


            }
        });

    }

    private void saveValueToFirebase() {

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setTitle("กรุณารอสักครู่ . . .");
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            //Success
                            String token = FirebaseInstanceId.getInstance().getToken();
                            String email = firebaseAuth.getCurrentUser().getEmail();
                            User user = new User(email, token, usernameString);
                            DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference(NODE_USER);
                            dbUser.child(firebaseAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toasty.success(Register.this, "ลงทะเบียนสำเร็จ",Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                            dbUser.child(firebaseAuth.getCurrentUser().getUid()).child("notificationLastSeen").setValue(day+"/"+(month+1)+"/"+(year+543)+" "+hours+":"+minute+":"+second);

                            sendVerificationEmail();

                            Register.this.getSupportFragmentManager().popBackStack();


                            finish();
                        } else {
                            //Have Error
                            Toasty.error(Register.this, "Cannot Register Please Try Again Register False Because " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void sendVerificationEmail() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.success(Register.this, "ลงทะเบียนเรียบร้อย", Toast.LENGTH_SHORT).show();
                                Toasty.info(Register.this, "ระบบได้ส่งอีเมล์ยืนตัวตนไปที่อีเมล์ "+user.getEmail()+" เรียบร้อยแล้ว โปรดยืนยันตัวตนเพื่อเข้าใช้งาน", Toast.LENGTH_LONG).show();
                            } else {
                                Toasty.error(Register.this, "ระบบผิดพลาดโปรดลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        firebaseAuth.signOut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
