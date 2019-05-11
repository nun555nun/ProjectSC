package com.example.projectsc;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.example.projectsc.login.NODE_fcm;


/**
 * A simple {@link Fragment} subclass.
 */
public class BinFragment extends Fragment {
    ListView listViewBin;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<UserBin> userBinList;
    FirebaseAuth auth;
    ArrayList<String> binArrayList;
    LayoutAnimationController controller;
    ConstraintLayout cl;
    Intent intent;
    Boolean a;
    String token;
    String binN;
    String[] type;

    public BinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        type = getResources().getStringArray(R.array.notitype2);
        token = FirebaseInstanceId.getInstance().getToken();
        View view = inflater.inflate(R.layout.fragment_bin, container, false);
        auth = FirebaseAuth.getInstance();
        a = true;
        binArrayList = new ArrayList<>();
        cl = view.findViewById(R.id.constraint_laout);
        listViewBin = view.findViewById(R.id.bin_list_view);
        userBinList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();

        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_slide_from_left);
        getBin();
        return view;
    }

    private void findUserBinNotification() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));

                    String notifyStatus1 = String.valueOf(BinSnapshot.child("notificationStatus").child("1").getValue());
                    String notifyStatus2 = String.valueOf(BinSnapshot.child("notificationStatus").child("2").getValue());
                    String notifyStatus3 = String.valueOf(BinSnapshot.child("notificationStatus").child("3").getValue());
                    String notifyStatus4 = String.valueOf(BinSnapshot.child("notificationStatus").child("4").getValue());
                    String notifyStatus5 = String.valueOf(BinSnapshot.child("notificationStatus").child("5").getValue());
                    String notifyStatus6 = String.valueOf(BinSnapshot.child("notificationStatus").child("6").getValue());
                    String notifyStatus7 = String.valueOf(BinSnapshot.child("notificationStatus").child("7").getValue());
                    String notifyStatus8 = String.valueOf(BinSnapshot.child("notificationStatus").child("8").getValue());
                    String notifyStatus9 = String.valueOf(BinSnapshot.child("notificationStatus").child("9").getValue());

                    if (notifyStatus1.equals("on")) {
                        setUserToken(bin, "1");
                    }
                    if (notifyStatus2.equals("on")) {
                        setUserToken(bin, "2");
                    }
                    if (notifyStatus3.equals("on")) {
                        setUserToken(bin, "3");
                    }
                    if (notifyStatus4.equals("on")) {
                        setUserToken(bin, "4");
                    }
                    if (notifyStatus5.equals("on")) {
                        setUserToken(bin, "5");
                    }
                    if (notifyStatus6.equals("on")) {
                        setUserToken(bin, "6");
                    }
                    if (notifyStatus7.equals("on")) {
                        setUserToken(bin, "7");
                    }
                    if (notifyStatus8.equals("on")) {
                        setUserToken(bin, "8");
                    }
                    if (notifyStatus9.equals("on")) {
                        setUserToken(bin, "9");
                    }
                }

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdaptor() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin");

        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userBinList.clear();
                progressDialog.cancel();

                for (String bin : binArrayList) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getKey().equals(bin)) {
                            Map map = (Map) data.getValue();
                            String binName = String.valueOf(map.get("binName"));
                            String startDate = String.valueOf(map.get("startDate"));
                            String temperature = String.valueOf(map.get("temp"));
                            String humidity = String.valueOf(map.get("humid"));

                            UserBin ub = new UserBin(binName, bin, temperature, humidity, startDate);
                            userBinList.add(ub);
                        }
                    }
                }
                if (userBinList.size() > 0 && getContext() != null) {

                    UserBinList adapter = new UserBinList(getContext(), userBinList);

                    listViewBin.setAdapter(adapter);
                    if (a) {
                        listViewBin.setLayoutAnimation(controller);
                        listViewBin.scheduleLayoutAnimation();
                        a = false;
                    }

                    for (int i = 0; i < userBinList.size(); i++) {
                        Log.v("test", userBinList.get(i).getBinID());
                    }
                    Log.v("test2", "--------------------------");
                    listViewBin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            intent = new Intent(getContext(), Navigationbottom.class);
                            Log.v("binName", userBinList.get(position).getBinID());
                            intent.putExtra("binID", userBinList.get(position).getBinID());
                            intent.putExtra("binName", userBinList.get(position).getBinName());
                            intent.putExtra("startDate", userBinList.get(position).getDate());
                            startActivity(intent);
                        }
                    });
                    listViewBin.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (isNetworkConnected()) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("ลบถัง")
                                        .setMessage("ต้องการลบถัง " + userBinList.get(position).getBinName()
                                                + " (" + userBinList.get(position).getBinID() + ")ใช่หรือไม่")
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteBin(userBinList.get(position).getBinID());
                                                Toasty.success(getContext(), "ลบถังเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            } else {
                                Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                            }


                            return true;
                        }
                    });

                }
                dbRef.removeEventListener(this);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteBin(final String userBin) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String binPart = ds.getKey();
                    Map map = (Map) ds.getValue();
                    String binID = String.valueOf(map.get("binid"));
                    if (binID.equals(userBin)) {
                        deleteUserBin(binPart);
                        removeToken(userBin);
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

    private void deleteUserBin(String binPart) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin").child(binPart);
        dbRef.removeValue();
    }

    private void removeToken(String binID) {
        for (int i = 1; i <= 9; i++) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(NODE_fcm + "/" + binID + "/" + i).child(token);
            dbRef.removeValue();
        }
    }

    public void getBin() {

        dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binArrayList.clear();

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));
                    binArrayList.add(bin);

                }
                if (binArrayList.size() > 0) {
                    if (getContext() != null) {
                        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_slide_from_left);
                        cl.setBackgroundResource(R.drawable.bg);
                        setAdaptor();
                    }


                } else {
                    cl.setBackgroundResource(R.drawable.bg);
                    progressDialog.cancel();
                    listViewBin.setAdapter(null);
                }
                //dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            getBin();
            findUserBinNotification();
        }

    }

    private void setUserToken(String binID, String typeNotify) {

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("fcm-token/" + binID + "/" + typeNotify);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbRef.child(token).child("token").setValue(token);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}