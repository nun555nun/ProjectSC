package com.example.projectsc;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static com.example.projectsc.login.NODE_fcm;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    public ProgressDialog progressDialog;
    AlertDialog dialog;
    EditText etID;
    EditText etName;
    TextView tvStartDate;
    private int day, month, year;
    private Calendar mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDate = Calendar.getInstance();

        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading.....");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.keepSynced(true);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.getChildrenCount() == 0) {
                    new AlertDialog.Builder(Main.this)
                            .setMessage("ตอนนี้คุณไม่ได้ทำการเชื่อมต่อถัง คุณต้องการเพิ่มถังตอนนี้หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addBin();
                                    // Toast.makeText(Main3Activity.this, "ได้ทำการเพิ่มถังเรียบร้อย", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Main.this, "เมื่อคุณต้องการเพิ่มถังสามารถกดปุ่ม + ด้านขวามือเพื่อทำการเพิ่มถัง", Toast.LENGTH_LONG).show();
                                }
                            })
                            .show();
                    dbRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBin();
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        final TextView Emailtextview = navHeaderView.findViewById(R.id.text_view_email);
        final TextView Userametextview = navHeaderView.findViewById(R.id.text_view_username);
        dbRef = database.getReference("users").child(auth.getUid());
        progressDialog.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (auth.getCurrentUser() != null) {
                    Map map = (Map) dataSnapshot.getValue();
                    String email = String.valueOf(map.get("email"));
                    String username = String.valueOf(map.get("username"));

                    Emailtextview.setText(email);
                    Userametextview.setText(username);
                    progressDialog.cancel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setTitle(R.string.home);
        BinFragment fragment = new BinFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fram, fragment);
        fragmentTransaction.commit();


    }

    private void hideKeybord() {
        etName.setFocusable(false);
        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etName.setFocusableInTouchMode(true);

                return false;
            }
        });

        etID.setFocusable(false);
        etID.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etID.setFocusableInTouchMode(true);

                return false;
            }
        });

    }

    private void addBin() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main.this);
        View mview = getLayoutInflater().inflate(R.layout.dialog_add, null);

        etName = mview.findViewById(R.id.et_bin_name);
        etID = mview.findViewById(R.id.et_bin_id);
        tvStartDate = mview.findViewById(R.id.tv_start_date);

        tvStartDate.setText(day + "/" + (month + 1) + "/" + (year+543));

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Main.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        tvStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + (year+543));
                        tvStartDate.setTextColor(BLACK);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        hideKeybord();

        Button addButton = mview.findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().length() > 0 && etID.getText().toString().trim().length() > 0) {
                    checkData();
                } else {
                    Toast.makeText(Main.this, "โปรดใส่ข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setView(mview);
        dialog = mBuilder.create();
        dialog.show();
    }

    private void checkData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(etID.getText().toString().trim())) {
                        check = true;
                        break;
                    }
                }
                dbRef.removeEventListener(this);
                if (check) {
                    setBinId();
                } else {
                    etID.setText("");
                    Toast.makeText(Main.this, "ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setBinId() {
        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = true;
                int count = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if (binID.equals(etID.getText().toString().trim())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    Map binId = new HashMap();
                    binId.put("binid", etID.getText().toString().trim());
                    dbRef.push().setValue(binId);
                    Toast.makeText(Main.this, "เพิ่มถังเรียบร้อย", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    dbRef.removeEventListener(this);
                    setBinData();

                } else {
                    dbRef.removeEventListener(this);
                    etID.setText("");
                    Toast.makeText(Main.this, "คุณเคยเพิ่มถังนี้ไปแล้ว", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setBinData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + etID.getText().toString().trim());
        dbRef.child("binName").setValue(etName.getText().toString());
        dbRef.child("startDate").setValue(tvStartDate.getText().toString());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setTitle(R.string.home);
            BinFragment fragment = new BinFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fram, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(Main.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(Main.this, Account.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            new AlertDialog.Builder(Main.this)
                    .setTitle("ต้องการออกจากระบบใช่หรือไม่")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Log.d("Token = ",""+FirebaseInstanceId.getInstance().getToken());

                            dbRef = database.getReference("users").child(auth.getUid());
                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map map = (Map) dataSnapshot.getValue();
                                    String x = String.valueOf(map.get("email"));

                                    //Toast.makeText(Main.this,"ออกจากระบบเรียบร้อย", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //ลบ token
                            String token = FirebaseInstanceId.getInstance().getToken();
                            dbRef = database.getReference(NODE_fcm + "/bin1").child(token);
                            dbRef.removeValue();

                            //logout
                            String s = auth.getUid();

                            Intent intent = new Intent(Main.this, login.class);
                            auth.signOut();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
