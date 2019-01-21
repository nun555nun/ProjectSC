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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.projectsc.login.NODE_USER;
import static com.example.projectsc.login.NODE_fcm;

public class register extends AppCompatActivity {
    private String usernameString, emailString, passwordString;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    EditText usernameEditText ;
    EditText emailEditText ;
    EditText passwordEditText ;
    TextView tv_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("สมัครสมาชิก");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                if(tv_pass.getText().equals("1")){
                    tv_pass.setText("0");
                    passwordEditText.setTransformationMethod(null);
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                }
                else{
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

        progressDialog = new ProgressDialog(register.this);
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
                                        Toast.makeText(register.this, "ลงทะเบียนสำเร็จ",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                           /* dbUser = FirebaseDatabase.getInstance().getReference(NODE_fcm + "/bin1");
                            dbUser.child(token).child("token").setValue(token);*/


                            register.this.getSupportFragmentManager().popBackStack();
                            finish();
                        } else {
                            //Have Error
                            Toast.makeText(register.this, "Cannot Register Please Try Again Register False Because " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

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
