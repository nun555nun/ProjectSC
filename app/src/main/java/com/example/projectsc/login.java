package com.example.projectsc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import es.dmoral.toasty.Toasty;

public class login extends AppCompatActivity {

    public static final String NODE_USER = "users";
    public static final String NODE_fcm = "fcm-token";

    private EditText editTextemail;
    private EditText editTextpass;
    private Button buttonlogin;
    public ProgressBar progressBar;
    public FirebaseAuth auth;
    public String token;
    Animation fromBottom;
    public TextView forgetText;
    TextView regiter;
    TextView tv_pass;
    public ProgressDialog progressDialog;

    TextView tv_verify;
    TextView tv_reVerify;
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);

        editTextemail = findViewById(R.id.eemail);
        editTextpass = findViewById(R.id.epass);
        progressBar = findViewById(R.id.progressBar);
        buttonlogin = findViewById(R.id.buttonlogin);
        forgetText = findViewById(R.id.forget_text_view);
        regiter = findViewById(R.id.textView8);

        tv_pass = findViewById(R.id.pass_show);
        tv_pass.setVisibility(View.GONE);

        tv_verify = findViewById(R.id.tv_verify);
        tv_reVerify = findViewById(R.id.tv_re_verify);

        cl = findViewById(R.id.cl_verify);

        editTextemail.setAnimation(fromBottom);
        editTextpass.setAnimation(fromBottom);
        buttonlogin.setAnimation(fromBottom);
        forgetText.setAnimation(fromBottom);
        regiter.setAnimation(fromBottom);
        tv_pass.setAnimation(fromBottom);

        hideKeybord();
        passwordHideShow();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");


        auth = FirebaseAuth.getInstance();
        token = FirebaseInstanceId.getInstance().getToken();
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(login.this, Main.class);
                startActivity(intent);
                finish();

            }

        }

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextemail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                if (isNetworkConnected()) {
                    final String email = editTextemail.getText().toString();
                    final String password = editTextpass.getText().toString();


                    if (email.isEmpty()) {
                        editTextemail.setError("โปรดกรอก email");
                        editTextemail.requestFocus();
                    }
                    if (password.isEmpty()) {
                        editTextpass.setError("ตัวอักษรอย่างน้อย6ตัวขึ้นไป");
                        editTextpass.requestFocus();
                    }
                    if (password.length() < 6) {
                        editTextpass.setError("ตัวอักษรอย่างน้อย6ตัวขึ้นไป");
                        editTextpass.requestFocus();
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        editTextemail.setError("โปรดระบุ email ให้ถูกต้อง");
                        editTextemail.requestFocus();
                    }
                    if (!email.isEmpty() && password.length() >= 6) {
                        progressDialog.show();
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        progressDialog.cancel();

                                        if (!task.isSuccessful()) {
                                            Toasty.error(login.this, "รหัสผ่าน หรือ  อีเมล์ ไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                                        } else {
                                            auth.getCurrentUser().reload();
                                            if (auth.getCurrentUser().isEmailVerified()) {
                                                //saveToken(token);
                                                Intent intent = new Intent(login.this, Main.class);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_top, R.anim.fade_out);
                                                finish();
                                            } else {
                                                // Toasty.warning(login.this,"โปรดยืนยันตัวตนก่อนเข้าใช้งาน",Toast.LENGTH_SHORT).show();
                                                cl.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    }
                                });
                    }
                } else {
                    Toasty.warning(login.this, "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }

            }
        });


        regiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    Intent intent = new Intent(login.this, Register.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Toasty.warning(login.this, "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });

        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {
                    Intent i = new Intent(login.this, ResetPassword.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toasty.warning(login.this, "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


            }
        });
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

    }

    private void passwordHideShow() {
        editTextpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextpass.getText().length() > 0) {
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
                    editTextpass.setTransformationMethod(null);
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_black_24dp);
                } else {
                    tv_pass.setText("1");
                    editTextpass.setTransformationMethod(new PasswordTransformationMethod());
                    tv_pass.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                }
            }
        });
    }


    private void hideKeybord() {

        editTextemail.setFocusable(false);
        editTextemail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextemail.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextpass.setFocusable(false);
        editTextpass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextpass.setFocusableInTouchMode(true);

                return false;
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) login.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void sendVerificationEmail() {
        new AlertDialog.Builder(login.this)
                .setTitle("ส่งอีเมล์เพื่อยืนยันที่อยู่อีเมล์")
                .setMessage("ต้องการส่งอีเมล์เพื่อยืนยันที่อยู่อีเมล์ ใช่หรือไม่ ?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            user.reload();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toasty.info(login.this, "ระบบได้ส่งอีเมล์เพื่อยืนยันที่อยู่อีเมล์ไปที่อีเมล์ " + user.getEmail() + " เรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toasty.error(login.this, "ระบบผิดพลาดกรุณารอสักครู่และโปรดลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();


    }
}
