package com.example.projectsc;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import es.dmoral.toasty.Toasty;

import static android.graphics.Color.BLACK;
import static com.example.projectsc.login.NODE_fcm;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    public ProgressDialog progressDialog;
    AlertDialog dialog;
    TextView etID;
    EditText etName;
    TextView tvStartDate;
    private int day, month, year;
    private Calendar mDate;
    String binIDFromQR;
    String binName;
    String token;
    private String startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        binIDFromQR = i.getStringExtra("binID");
        token = FirebaseInstanceId.getInstance().getToken();

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
        if (auth.getCurrentUser() != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
            setCurrentDeviceToken();
        }
        // dbRef.keepSynced(true);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.getChildrenCount() == 0) {
                    dbRef.removeEventListener(this);
                    if (binIDFromQR == null) {
                        if (Main.this != null) {
                            new AlertDialog.Builder(Main.this)
                                    .setMessage("ตอนนี้คุณไม่ได้ทำการเชื่อมต่อถัง คุณต้องการเพิ่มถังตอนนี้หรือไม่")
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(Main.this, QRMainActivity.class);
                                            startActivity(i);

                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toasty.info(Main.this, "เมื่อคุณต้องการเพิ่มถังสามารถกดปุ่ม + ด้านขวามือเพื่อทำการเพิ่มถัง", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .show();

                        }
                    }
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

               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if (isNetworkConnected()) {
                    Intent i = new Intent(Main.this, QRMainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Snackbar.make(view, "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Toast.makeText(Main.this, "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }


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
        final TextView EmailTextView = navHeaderView.findViewById(R.id.text_view_email);
        final TextView UsernameTextView = navHeaderView.findViewById(R.id.text_view_username);
        dbRef = database.getReference("users").child(auth.getUid());
        progressDialog.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (auth.getCurrentUser() != null) {
                    Map map = (Map) dataSnapshot.getValue();
                    String email = String.valueOf(map.get("email"));
                    String username = String.valueOf(map.get("username"));

                    EmailTextView.setText(email);
                    UsernameTextView.setText(username);
                    progressDialog.cancel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (binIDFromQR != null) {

            getStartDate();
            checkData();

        }

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
    }

    private void addDataBin() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main.this);
        View mview = getLayoutInflater().inflate(R.layout.dialog_add, null);

        etName = mview.findViewById(R.id.et_bin_name);

        if (binName != null && binName.length() > 0) {
            etName.setHint(binName);
        }
        etID = mview.findViewById(R.id.et_bin_id);
        tvStartDate = mview.findViewById(R.id.tv_start_date);
        if (binIDFromQR.length() > 0) {
            etID.setText(binIDFromQR);
        }


        if (startDate == null) {
            tvStartDate.setText(day + "/" + (month + 1) + "/" + (year + 543));
        } else {
            tvStartDate.setText(startDate);
        }

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Main.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        tvStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + (year + 543));
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
                dialog.dismiss();
                if(isNetworkConnected()){
                    setUserBin();
                }
                else {
                    Toasty.error(Main.this, "ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้", Toast.LENGTH_LONG).show();
                }

            }
        });
        mBuilder.setView(mview);
        dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void getStartDate() {

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binIDFromQR + "/" + "startDate");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startDate = dataSnapshot.getValue(String.class);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUserBin() {

        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map binId = new HashMap();
                binId.put("binid", binIDFromQR.trim());
                dbRef.push().setValue(binId);

                dbRef.removeEventListener(this);
                setBinData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkData() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(binIDFromQR.trim())) {
                        Map map = (Map) ds.getValue();
                        binName = String.valueOf(map.get("binName"));
                        check = true;
                        break;
                    }
                }
                dbRef.removeEventListener(this);
                if (check) {

                    checkBinId();
                } else {
                    Toasty.error(Main.this, "ไม่มี รหัสถังนี้ในระบบ หรือ ถังยังไม่ได้เปิดใช้", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkBinId() {
        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean check = true;
                int count = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if (binID.equals(binIDFromQR.trim())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    dbRef.removeEventListener(this);
                    addDataBin();

                } else {
                    dbRef.removeEventListener(this);
                    Toasty.error(Main.this, "คุณเคยเพิ่มถังนี้ไปแล้ว", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void setBinData() {
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binIDFromQR.trim());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (etName.getText().toString().length() == 0) {
                    dbRef.child("binName").setValue(etName.getHint().toString());
                } else {
                    dbRef.child("binName").setValue(etName.getText().toString());
                }

                dbRef.child("startDate").setValue(tvStartDate.getText().toString());
                Toasty.success(Main.this, "เพิ่มถังเรียบร้อย", Toast.LENGTH_SHORT).show();
                dbRef.removeEventListener(this);
                setUserToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserToken() {

        dbRef = FirebaseDatabase.getInstance().getReference("fcm-token/" + binIDFromQR.trim());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                dbRef.child("1").child(token).child("token").setValue(token);
                dbRef.child("2").child(token).child("token").setValue(token);
                dbRef.child("3").child(token).child("token").setValue(token);
                dbRef.child("4").child(token).child("token").setValue(token);
                dbRef.child("5").child(token).child("token").setValue(token);
                dbRef.child("6").child(token).child("token").setValue(token);
                dbRef.child("7").child(token).child("token").setValue(token);
                dbRef.child("8").child(token).child("token").setValue(token);
                dbRef.child("9").child(token).child("token").setValue(token);

                dbRef.removeEventListener(this);
                setNotificationStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNotificationStatus() {
        final String binID = binIDFromQR.trim();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String binid = String.valueOf(map.get("binid"));
                    String binPart = BinSnapshot.getKey();
                    if (binid.equals(binID)) {
                        for (int i = 1; i <= 9; i++) {
                            dbRef.child(binPart).child("notificationStatus").child(String.valueOf(i)).setValue("on");
                        }
                        break;
                    }
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            Intent intent = new Intent(Main.this, SaveNotificationAll.class);
            startActivity(intent);
        } else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(Main.this, Account.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            if (!isNetworkConnected()) {
                new AlertDialog.Builder(Main.this)
                        .setTitle("ไม่มีการเชื่อมต่ออินเตอร์เน็ต")
                        .setMessage("ขณะนี้ท่านไม่ได้มีการเชื่อมต่ออินเตอร์เน็ตอยู่ หากท่านออกจากระบบตอนนี้ อาจยังทำให้ท่านได้รับการแจ้งเตือนต่างๆอยู่ หากท่านไม่ต้องการรับการแจ้งเตือน กรุณาเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน")
                        .setPositiveButton("ต่อไป", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(Main.this)
                                        .setTitle("ต้องการออกจากระบบใช่หรือไม่")
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                findUserBin();

                                                String s = auth.getUid();

                                                auth.signOut();
                                                finish();
                                                /*Intent intent = new Intent(Main.this, login.class);
                                                startActivity(intent);*/
                                                Intent intent = new Intent(getApplicationContext(), login.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                            }
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            }
                        })
                        .setNegativeButton("ยกเลิก", null)
                        .show();
            } else {
                new AlertDialog.Builder(Main.this)
                        .setTitle("ต้องการออกจากระบบใช่หรือไม่")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                findUserBin();

                                String s = auth.getUid();

                                Intent intent = new Intent(Main.this, login.class);
                                auth.signOut();
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findUserBin() {

        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    removeToken(binID);
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeToken(String binID) {
        for (int i = 1; i <= 9; i++) {
            DatabaseReference dbRef = database.getReference(NODE_fcm + "/" + binID + "/" + i).child(token);
            dbRef.removeValue();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) Main.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void setCurrentDeviceToken(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid()).child("token");
        dbRef.setValue(token);
    }
}
