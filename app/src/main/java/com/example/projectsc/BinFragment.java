package com.example.projectsc;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
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
    public BinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
        //removeLogNotification();
        //findUserBinNotification();
        controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_slide_from_left);
        getBin();
        return view;
    }

    private void removeLogNotification() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
        dbRef.removeValue();
    }

    private void findLogNotification(final String bin) {
        getbinName(bin);
        for (int i = 1; i <= 8; i++) {

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("notification/" + bin + "/" + String.valueOf(i));

            final int finalI = i;
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String[] type = getResources().getStringArray(R.array.notitype2);
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogNotification logNotification = logDHTSnapshot.getValue(LogNotification.class);

                        Map binId = new HashMap();
                        binId.put("binName",binN);
                        binId.put("binId", bin);
                        binId.put("date", logNotification.getDate());
                        binId.put("time", logNotification.getTime());
                        binId.put("type", type[finalI - 1]);

                        saveLogNotification(binId);

                    }
                    dbRef.removeEventListener(this);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void getbinName(String bin) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + bin + "/" + "binName");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                binN =  dataSnapshot.getValue(String.class);
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveLogNotification(Map binId) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/logNotification");
        dbRef.push().setValue(binId);
    }

    private void findUserBinNotification() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String bin = String.valueOf(map.get("binid"));
                    findLogNotification(bin);
                    String notifyStatus1 = String.valueOf(BinSnapshot.child("notificationStatus").child("1").getValue());
                    String notifyStatus2 = String.valueOf(BinSnapshot.child("notificationStatus").child("2").getValue());
                    String notifyStatus3 = String.valueOf(BinSnapshot.child("notificationStatus").child("3").getValue());
                    String notifyStatus4 = String.valueOf(BinSnapshot.child("notificationStatus").child("4").getValue());
                    String notifyStatus5 = String.valueOf(BinSnapshot.child("notificationStatus").child("5").getValue());
                    String notifyStatus6 = String.valueOf(BinSnapshot.child("notificationStatus").child("6").getValue());
                    String notifyStatus7 = String.valueOf(BinSnapshot.child("notificationStatus").child("7").getValue());
                    String notifyStatus8 = String.valueOf(BinSnapshot.child("notificationStatus").child("8").getValue());

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
////////////////////////////////////////////////////

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
                            //Toast.makeText(getContext(), bin + " add", Toast.LENGTH_SHORT).show();
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
                   /* listViewBin.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            return false;
                        }
                    });*/

                }
                dbRef.removeEventListener(this);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    cl.setBackgroundResource(R.drawable.bg_block);
                    progressDialog.cancel();
                    listViewBin.setAdapter(null);

                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        removeLogNotification();
        findUserBinNotification();
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

}