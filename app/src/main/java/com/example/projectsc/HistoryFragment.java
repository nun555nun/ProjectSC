package com.example.projectsc;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    ListView listViewLogDHT;
    DatabaseReference dbRef;
    public ProgressDialog progressDialog;
    List<LogDHT> logDHTList;

String logDHTType;
    String binID;
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binID = getArguments().getString("binID");
        logDHTType = getArguments().getString("logDHT");
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/"+logDHTType);

        listViewLogDHT = view.findViewById(R.id.list_view_logDHT);
        logDHTList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.setTitle("กำลังโหลดข้อมูล");
        progressDialog.show();
        setAdaptor();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        setAdaptor();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdaptor();
    }
    public void setAdaptor(){
        progressDialog.show();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null){
                    dbRef.removeEventListener(this);
                }
                else {
                    LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_fall_down);
                    logDHTList.clear();
                    for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                        LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                        logDHTList.add(logDHT);
                    }
                    if (logDHTList.size() > 0 && getContext() != null) {
                        progressDialog.dismiss();
                        LogDHTList adapter = new LogDHTList(getContext(), logDHTList);

                        listViewLogDHT.setAdapter(adapter);
                        listViewLogDHT.setLayoutAnimation(controller);
                        listViewLogDHT.scheduleLayoutAnimation();
                    }
                    progressDialog.dismiss();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
