package com.example.projectsc;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {
    private String nameString, emailString, passwordString;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createToolbar();
    }

    private void createToolbar() {
        Button b = findViewById(R.id.button5);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText nameEditText = findViewById(R.id.editname);
                EditText emailEditText = findViewById(R.id.editemail);
                EditText passwordEditText = findViewById(R.id.editpass);

                nameString = nameEditText.getText().toString().trim();
                emailString = emailEditText.getText().toString().trim();
                passwordString = passwordEditText.getText().toString().trim();

                if (!nameString.isEmpty() && !emailString.isEmpty() && !passwordString.isEmpty()) {
//                    No Space
                    saveValueToFirebase();
                } else {
                    Toast.makeText(register.this,"โปรดกรอกข้อมูลให้ครบ",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(register.this, "ลงทะเบียนสำเร็จ",
                                    Toast.LENGTH_SHORT).show();
                            register.this.getSupportFragmentManager().popBackStack();
                            finish();
                        } else {
                            //Have Error
                            Toast.makeText(register.this, "Cannot Register Please Try Again Register False Because " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }
}