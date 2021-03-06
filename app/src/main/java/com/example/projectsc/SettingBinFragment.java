package com.example.projectsc;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.example.projectsc.login.NODE_fcm;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingBinFragment extends Fragment {

    Switch switchAll;
    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;
    Switch switch5;
    Switch switch6;
    Switch switch7;
    Switch switch8;
    Switch switch9;

    ConstraintLayout c;
    public DatabaseReference dbRef;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    String token;
    String binID;

    EditText editTextFillAir;
    EditText editTextFillWater;
    EditText editTextTempMax;
    EditText editTextTempMin;
    EditText editTextHumidMax;
    EditText editTextHumidMin;
    EditText editTextBinName;

    Button waterSettingButton;
    Button airSettingButton;
    Button tempSettingButton;
    Button humidSettingButton;
    Button resetSettingButton;
    Button clearDataButton;
    Button binNameButton;
    Button timeButton;
    Button restartButton;


    ConstraintLayout cs;
    ScrollView sv;


    int countOn;
    int countCheckOn;

    private int day, month, year;
    private Calendar mDate;
    private Spinner s;
    private String[] type;

    AnimationDrawable networkAnimation;

    public SettingBinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_bin, container, false);
        binID = getArguments().getString("binID");
        Log.d("binID", binID);

        s = view.findViewById(R.id.spinner_time_fre);
        type = new String[]{"3", "10", "20"};
        ArrayAdapter<String> adapterTimeF = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, type);
        s.setAdapter(adapterTimeF);

        mDate = Calendar.getInstance();

        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);

        ImageView imageView = view.findViewById(R.id.iv_wifi);
        imageView.setBackgroundResource(R.drawable.animation);
        networkAnimation = (AnimationDrawable) imageView.getBackground();
        networkAnimation.start();

        c = view.findViewById(R.id.notify_constranlayout);
        cs = view.findViewById(R.id.not_connect_cl);
        sv = view.findViewById(R.id.scrollview_setting);
        if (isNetworkConnected()) {
            sv.setVisibility(View.VISIBLE);
            cs.setVisibility(View.GONE);
        } else {
            sv.setVisibility(View.GONE);
            cs.setVisibility(View.VISIBLE);
            c.setClickable(false);
        }
        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    sv.setVisibility(View.VISIBLE);
                    cs.setVisibility(View.GONE);
                } else {
                    sv.setVisibility(View.GONE);
                    cs.setVisibility(View.VISIBLE);
                }
            }
        });
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        dbRef = database.getReference();
        token = FirebaseInstanceId.getInstance().getToken();


        switchAll = view.findViewById(R.id.switch_all);
        switch1 = view.findViewById(R.id.switch_1);
        switch2 = view.findViewById(R.id.switch_2);
        switch3 = view.findViewById(R.id.switch_3);
        switch4 = view.findViewById(R.id.switch_4);
        switch5 = view.findViewById(R.id.switch_5);
        switch6 = view.findViewById(R.id.switch_6);
        switch7 = view.findViewById(R.id.switch_7);
        switch8 = view.findViewById(R.id.switch_8);
        switch9 = view.findViewById(R.id.switch_9);

        editTextFillAir = view.findViewById(R.id.et_fill_air);
        editTextFillWater = view.findViewById(R.id.et_fill_water);
        editTextTempMax = view.findViewById(R.id.et_temp_max);
        editTextTempMin = view.findViewById(R.id.et_temp_min);
        editTextHumidMax = view.findViewById(R.id.et_humid_max);
        editTextHumidMin = view.findViewById(R.id.et_humid_min);
        editTextBinName = view.findViewById(R.id.bin_name_et);

        airSettingButton = view.findViewById(R.id.air_setting_button);
        waterSettingButton = view.findViewById(R.id.water_setting_button);
        tempSettingButton = view.findViewById(R.id.temp_setting_button);
        humidSettingButton = view.findViewById(R.id.humid_setting_button);
        resetSettingButton = view.findViewById(R.id.reset_setting);
        clearDataButton = view.findViewById(R.id.clear_bin_data);
        binNameButton = view.findViewById(R.id.bin_name_button);
        timeButton = view.findViewById(R.id.time_fre_button);
        restartButton = view.findViewById(R.id.restart_bin);

        hideKeyBord();

        setSwitch();

        setHint();

        onSwitchChange();

        onButtonClick();

        return view;
    }

    private void onButtonClick() {


        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        airSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (editTextFillAir.getText().length() > 0 && Integer.parseInt(editTextFillAir.getText().toString()) > 0) {
                        if (!editTextFillAir.getText().toString().equals(editTextFillAir.getHint().toString())) {
                            editTextFillAir.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            new AlertDialog.Builder(getContext())
                                    .setTitle("แก้ไข ระยะเวลาในการเติมอากาศ")
                                    .setMessage("ต้องการเปลี่ยน จาก " + editTextFillAir.getHint() + " นาที เป็น " + Integer.parseInt(editTextFillAir.getText().toString()) + " นาที ใช่หรือไม่")
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dbRef.child("delayAir").setValue(Integer.parseInt(editTextFillAir.getText().toString()));
                                            dbRef.child("statusAir").setValue(1);
                                            dbRef.child("statuswork").setValue(1);
                                            editTextFillAir.setHint(String.valueOf(Integer.parseInt(editTextFillAir.getText().toString())));
                                            editTextFillAir.setText("");
                                            editTextFillAir.setFocusable(false);
                                            Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        } else {
                            editTextFillAir.setText("");
                            editTextFillAir.onEditorAction(EditorInfo.IME_ACTION_DONE);

                        }
                    } else {
                        Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }

            }
        });

        waterSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    if (editTextFillWater.getText().length() > 0 && Integer.parseInt(editTextFillWater.getText().toString()) > 0) {
                        if (Integer.parseInt(editTextFillWater.getText().toString()) <= 20) {
                            if (!editTextFillWater.getText().toString().equals(editTextFillWater.getHint().toString())) {
                                editTextFillWater.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                new AlertDialog.Builder(getContext())
                                        .setTitle("แก้ไข ระยะเวลาในการเติมน้ำ")
                                        .setMessage("ต้องการเปลี่ยน จาก " + editTextFillWater.getHint() + " วินาที เป็น " + Integer.parseInt(editTextFillWater.getText().toString()) + " วินาที ใช่หรือไม่")
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dbRef.child("delayWater").setValue(Integer.parseInt(editTextFillWater.getText().toString()));

                                                dbRef.child("statusWater").setValue(1);

                                                dbRef.child("statuswork").setValue(1);
                                                editTextFillWater.setHint(String.valueOf(Integer.parseInt(editTextFillWater.getText().toString())));
                                                editTextFillWater.setText("");
                                                editTextFillWater.setFocusable(false);
                                                Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();


                            } else {
                                editTextFillWater.setText("");

                                editTextFillWater.onEditorAction(EditorInfo.IME_ACTION_DONE);

                            }
                        } else {
                            Toasty.warning(getContext(), "จำกัดเวลาเติมน้ำมากสุด 20 วินาที", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tempSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    String tempMax = editTextTempMax.getText().toString();
                    String tempMin = editTextTempMin.getText().toString();
                    int max;
                    int min;
                    if (tempMax.length() > 0 && tempMin.length() > 0) {
                        max = Integer.parseInt(editTextTempMax.getText().toString());
                        min = Integer.parseInt(editTextTempMin.getText().toString());
                        if (max > 0 && min > 0) {
                            if (checkMax(max, min) && checkMin(max, min)) {

                                if (max <= 100 && min >= 0) {
                                    editTextTempMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                    editTextTempMin.onEditorAction(EditorInfo.IME_ACTION_DONE);

                                    if (max != Integer.parseInt(editTextTempMax.getHint().toString()) && min != Integer.parseInt(editTextTempMin.getHint().toString())) {
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("แก้ไข " + getString(R.string.temperature))
                                                .setMessage("ต้องการเปลี่ยน" + getString(R.string.maximum) + "จาก " + Integer.parseInt(editTextTempMax.getHint().toString()) + " °C เป็น " + Integer.parseInt(editTextTempMax.getText().toString()) + " °C และ "
                                                        + "ต้องการเปลี่ยน" + getString(R.string.minimum) + "จาก " + Integer.parseInt(editTextTempMin.getHint().toString()) + " °C เป็น " + Integer.parseInt(editTextTempMin.getText().toString()) + " °C ใช่หรือไม่"
                                                )
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        dbRef.child("tempMax").setValue(Integer.parseInt(editTextTempMax.getText().toString()) + ".00 °C");
                                                        dbRef.child("tempMin").setValue(Integer.parseInt(editTextTempMin.getText().toString()) + ".00 °C");
                                                        editTextTempMax.setHint(String.valueOf(Integer.parseInt(editTextTempMax.getText().toString())));
                                                        editTextTempMin.setHint(String.valueOf(Integer.parseInt(editTextTempMin.getText().toString())));

                                                        dbRef.child("statustempMax").setValue(1);
                                                        dbRef.child("statustempMin").setValue(1);
                                                        dbRef.child("statusrun").setValue(1);
                                                        editTextTempMax.setText("");
                                                        editTextTempMin.setText("");

                                                        editTextTempMax.setFocusable(false);
                                                        editTextTempMin.setFocusable(false);

                                                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.no, null)
                                                .show();
                                    } else if (max == Integer.parseInt(editTextTempMax.getHint().toString()) && min != Integer.parseInt(editTextTempMin.getHint().toString())) {
                                        changeTempMin();
                                    } else if (max != Integer.parseInt(editTextTempMax.getHint().toString()) && min == Integer.parseInt(editTextTempMin.getHint().toString())) {
                                        changeTempMax();
                                    } else {
                                        editTextTempMax.setText("");
                                        editTextTempMin.setText("");

                                    }
                                } else if (!(max <= 100)) {
                                    Toasty.error(getContext(), "ค่าสูงสุดไม่สามารถตั้งค่ามากกว่า 70 องศาเซลเซียสได้", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(getContext(), "ค่าต่ำสุดไม่สามารถตั้งค่าน้อยกว่า 32 องศาเซลเซียสได้", Toast.LENGTH_SHORT).show();
                                }


                            } else if (max == min) {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else if (!checkMax(max, min)) {
                                Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();

                            } else {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ มากกว่า " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                        }

                    } else if (tempMax.length() > 0) {
                        max = Integer.parseInt(editTextTempMax.getText().toString());
                        min = Integer.parseInt(editTextTempMin.getHint().toString());

                        if (checkMax(max, min) && checkMin(max, min)) {
                            if (max <= 100) {
                                if (max != Integer.parseInt(editTextTempMax.getHint().toString())) {
                                    changeTempMax();
                                } else {
                                    editTextTempMax.setText("");

                                }
                            } else {
                                Toasty.error(getContext(), "ค่าสูงสุดไม่สามารถตั้งค่ามากกว่า 70 องศาเซลเซียสได้", Toast.LENGTH_SHORT).show();
                            }

                        } else if (max == min) {
                            Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                        } else if (!checkMax(max, min)) {
                            Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ มากกว่า " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();
                        }

                    } else if (tempMin.length() > 0) {

                        max = Integer.parseInt(editTextTempMax.getHint().toString());
                        min = Integer.parseInt(editTextTempMin.getText().toString());
                        if (min != 0) {

                            if (checkMax(max, min) && checkMin(max, min)) {
                                if (min >= 0) {
                                    if (min != Integer.parseInt(editTextTempMin.getHint().toString())) {
                                        changeTempMin();
                                    } else {
                                        editTextTempMin.setText("");
                                    }
                                } else {
                                    Toasty.error(getContext(), "ค่าต่ำสุดไม่สามารถตั้งค่าน้อยกว่า 32 องศาเซลเซียสได้", Toast.LENGTH_SHORT).show();
                                }

                            } else if (max == min) {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else if (!checkMax(max, min)) {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ มากกว่า " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else {
                                Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }
            }
        });

        humidSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    String humidMax = editTextHumidMax.getText().toString();
                    String humidMin = editTextHumidMin.getText().toString();
                    int max;
                    int min;
                    if (humidMax.length() > 0 && humidMin.length() > 0) {
                        max = Integer.parseInt(editTextHumidMax.getText().toString());
                        min = Integer.parseInt(editTextHumidMin.getText().toString());
                        if (max > 0 && min > 0) {
                            if (checkMax(max, min) && checkMin(max, min)) {
                                if (max <= 100 && min >= 0) {
                                    if (max != Integer.parseInt(editTextHumidMax.getHint().toString()) && min != Integer.parseInt(editTextHumidMin.getHint().toString())) {
                                        editTextHumidMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                        editTextHumidMin.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("แก้ไข " + getString(R.string.humidity))
                                                .setMessage("ต้องการเปลี่ยน" + getString(R.string.maximum) + "จาก " + Integer.parseInt(editTextHumidMax.getHint().toString()) + " % เป็น " + Integer.parseInt(editTextHumidMax.getText().toString()) + " % และ "
                                                        + "ต้องการเปลี่ยน" + getString(R.string.minimum) + "จาก " + Integer.parseInt(editTextHumidMin.getHint().toString()) + " % เป็น " + Integer.parseInt(editTextHumidMin.getText().toString()) + " % ใช่หรือไม่"
                                                )
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dbRef.child("humidMax").setValue(Integer.parseInt(editTextHumidMax.getText().toString()) + ".00 %");
                                                        dbRef.child("humidMin").setValue(Integer.parseInt(editTextHumidMin.getText().toString()) + ".00 %");
                                                        editTextHumidMax.setHint(String.valueOf(Integer.parseInt(editTextHumidMax.getText().toString())));
                                                        editTextHumidMin.setHint(String.valueOf(Integer.parseInt(editTextHumidMin.getText().toString())));

                                                        dbRef.child("statushumidMax").setValue(1);
                                                        dbRef.child("statushumidMin").setValue(1);
                                                        dbRef.child("statusrun").setValue(1);

                                                        editTextHumidMax.setText("");
                                                        editTextHumidMin.setText("");


                                                        editTextHumidMax.setFocusable(false);
                                                        editTextHumidMin.setFocusable(false);

                                                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton(R.string.no, null)
                                                .show();
                                    } else if (max == Integer.parseInt(editTextHumidMax.getHint().toString()) && min != Integer.parseInt(editTextHumidMin.getHint().toString())) {
                                        changeHumidMin();
                                    } else if (max != Integer.parseInt(editTextHumidMax.getHint().toString()) && min == Integer.parseInt(editTextHumidMin.getHint().toString())) {
                                        changeHumidMax();
                                    } else {
                                        editTextHumidMax.setText("");
                                        editTextHumidMin.setText("");

                                    }
                                } else if (!(max <= 100)) {
                                    Toasty.error(getContext(), "ค่าสูงสุดไม่สามารถตั้งค่ามากกว่า 70 เปอร์เซ็นได้", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(getContext(), "ค่าต่ำสุดไม่สามารถตั้งค่าน้อยกว่า 40 เปอร์เซ็นได้", Toast.LENGTH_SHORT).show();
                                }


                            } else if (max == min) {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else if (!checkMax(max, min)) {
                                Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();

                            } else {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ มากกว่า " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                        }

                    } else if (humidMax.length() > 0) {
                        max = Integer.parseInt(editTextHumidMax.getText().toString());
                        min = Integer.parseInt(editTextHumidMin.getHint().toString());

                        if (checkMax(max, min) && checkMin(max, min)) {
                            if (max <= 100) {
                                if (max != Integer.parseInt(editTextHumidMax.getHint().toString())) {
                                    changeHumidMax();
                                } else {
                                    editTextHumidMax.setText("");
                                }
                            } else {
                                Toasty.error(getContext(), "ค่าสูงสุดไม่สามารถตั้งค่ามากกว่า 70 เปอร์เซ็นได้", Toast.LENGTH_SHORT).show();
                            }

                        } else if (max == min) {
                            Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                        } else if (!checkMax(max, min)) {

                            Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();

                        } else if (max == Integer.parseInt(editTextHumidMax.getHint().toString())) {
                            editTextHumidMax.setText("");

                        }

                    } else if (humidMin.length() > 0) {

                        max = Integer.parseInt(editTextHumidMax.getHint().toString());
                        min = Integer.parseInt(editTextHumidMin.getText().toString());
                        if (min != 0) {

                            if (checkMax(max, min) && checkMin(max, min)) {
                                if (min >= 0) {
                                    if (min != Integer.parseInt(editTextHumidMin.getHint().toString())) {
                                        changeHumidMin();
                                    } else {
                                        editTextHumidMin.setText("");
                                    }
                                } else {
                                    Toasty.error(getContext(), "ค่าต่ำสุดไม่สามารถตั้งค่าน้อยกว่า 40 เปอร์เซ็นได้", Toast.LENGTH_SHORT).show();
                                }

                            } else if (max == min) {
                                Toasty.error(getContext(), getString(R.string.minimum) + " ไม่สามารถ เท่ากับ " + getString(R.string.maximum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else if (!checkMax(max, min)) {

                                Toasty.error(getContext(), getString(R.string.maximum) + " ไม่สามารถ น้อยกว่า " + getString(R.string.minimum) + " ได้", Toast.LENGTH_SHORT).show();
                            } else if (min == Integer.parseInt(editTextHumidMin.getHint().toString())) {
                                editTextHumidMin.setText("");

                            }
                        } else {
                            Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toasty.error(getContext(), "กรุณากรอกตัวเลขจำนวนเต็มที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }

            }
        });

        resetSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("รีเซตการตั้งค่า ")
                            .setMessage("ต้องการรีเซตการตั้งค่า ใช่หรือไม่ ?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setDefault();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }

            }
        });

        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    clearData();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkConnected()) {
                    if (editTextBinName.getText().toString().length() == 0) {
                        Toasty.error(getContext(), "กรุณากรอกชื่อถัง", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!editTextBinName.getText().toString().equals(editTextBinName.getHint().toString())) {
                            changeBinName();
                        } else {
                            editTextBinName.setText("");
                        }
                    }

                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    changeTime();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected()) {
                    restartBin();
                } else {
                    Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void restartBin() {

        new AlertDialog.Builder(getContext())
                .setTitle("ต้องการเปิดถังใหม่ใช่หรือไม่")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
                        dbRef.child("statusTurnback").setValue(1);
                        Toasty.success(getContext(), "เริ่มใหม่เรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changeTime() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map map = (Map) dataSnapshot.getValue();
                String time = String.valueOf(map.get("timesensor"));

                if (time.equals(s.getSelectedItem().toString())) {
                    Toasty.warning(getContext(), "ขณะนี้ได้ตั้งค่าไว้ที่ " + time + " นาที อยู่แล้ว", Toasty.LENGTH_SHORT).show();
                } else {

                    new AlertDialog.Builder(getContext())
                            .setTitle("แก้ไขความถี่ในการเก็บข้อมูล")
                            .setMessage("ต้องการเปลี่ยนความถี่ในการเก็บข้อมูลจาก " + time + " นาทีเป็น " + s.getSelectedItem().toString() + " นาทีใช่หรือไม่")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dbRef.child("timesensor").setValue(Integer.parseInt(s.getSelectedItem().toString()));
                                    dbRef.child("statustime").setValue(1);

                                    Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void changeBinName() {

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        editTextBinName.onEditorAction(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(getContext())
                .setTitle("แก้ไขชื่อถัง")
                .setMessage("ต้องการเปลี่ยนชื่อถังจาก " + editTextBinName.getHint().toString() + " เป็น " + editTextBinName.getText().toString() + " ใช่หรือไม่")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbRef.child("binName").setValue(editTextBinName.getText().toString());
                        editTextBinName.setHint(editTextBinName.getText().toString());

                        editTextBinName.setText("");

                        editTextBinName.setFocusable(false);

                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    private void clearData() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        new AlertDialog.Builder(getContext())
                .setTitle("เริ่มการหมักใหม่")
                .setMessage("ต้องการล้างประวัติทั้งหมด ใช่หรือไม่ ?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbRef.child("statusTurnback").setValue(1);
                        dbRef.child("statusWaterWork").setValue(0);
                        dbRef.child("statusAirWork").setValue(0);

                        dbRef.child("temp").setValue("-");
                        dbRef.child("humid").setValue("-");
                        dbRef.child("time").setValue("-");

                        removeHistory();
                        changeStartDate();
                        new AlertDialog.Builder(getContext())
                                .setTitle("รีเซตการตั้งค่า")
                                .setMessage("ต้องการรีเซตการตั้งค่า หรือไม่ ?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setDefault();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                        Toasty.success(getContext(), "รีเซตถังเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changeStartDate() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/startDate");
        dbRef.setValue(day + "/" + (month + 1) + "/" + (year + 543));
    }

    private void removeHistory() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date");
        dbRef.removeValue();
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/date_time");
        dbRef.removeValue();
        dbRef = FirebaseDatabase.getInstance().getReference("notification/" + binID);
        dbRef.removeValue();
        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
        dbRef.removeValue();
    }

    private void setDefault() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);

        dbRef.child("delayAir").setValue(10);
        dbRef.child("delayWater").setValue(1);
        dbRef.child("tempMax").setValue("70.00 °C");
        dbRef.child("tempMin").setValue("32.00 °C");
        dbRef.child("humidMax").setValue("70.00 %");
        dbRef.child("humidMin").setValue("40.00 %");

        dbRef.child("statusAir").setValue(0);
        dbRef.child("statusWater").setValue(0);
        dbRef.child("statusDefault").setValue(1);
        dbRef.child("statustempMax").setValue(0);
        dbRef.child("statustempMin").setValue(0);
        dbRef.child("statushumidMax").setValue(0);
        dbRef.child("statushumidMin").setValue(0);

        dbRef.child("timesensor").setValue(3);

        editTextFillAir.setHint("10");
        editTextFillWater.setHint("1");
        editTextTempMax.setHint("70");
        editTextTempMin.setHint("32");
        editTextHumidMax.setHint("70");
        editTextHumidMin.setHint("40");

        editTextFillAir.setText("");
        editTextFillWater.setText("");
        editTextTempMax.setText("");
        editTextTempMin.setText("");
        editTextHumidMax.setText("");
        editTextHumidMin.setText("");

        switchAll.setChecked(true);
        switch1.setChecked(true);
        switch2.setChecked(true);
        switch3.setChecked(true);
        switch4.setChecked(true);
        switch5.setChecked(true);
        switch6.setChecked(true);
        switch7.setChecked(true);
        switch8.setChecked(true);
        switch9.setChecked(true);

        s.setSelection(0);
        Toasty.success(getContext(), "รีเซตการตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();

    }

    private void changeHumidMax() {
        editTextHumidMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTextHumidMin.onEditorAction(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(getContext())
                .setTitle("แก้ไข " + getString(R.string.humidity))
                .setMessage("ต้องการเปลี่ยน" + getString(R.string.maximum) + "จาก " + Integer.parseInt(editTextHumidMax.getHint().toString()) + " % เป็น " + Integer.parseInt(editTextHumidMax.getText().toString()) + " % ใช่หรือไม่"
                )
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbRef.child("humidMax").setValue(Integer.parseInt(editTextHumidMax.getText().toString()) + ".00 %");
                        editTextHumidMax.setHint(String.valueOf(Integer.parseInt(editTextHumidMax.getText().toString())));

                        dbRef.child("statushumidMax").setValue(1);
                        dbRef.child("statusrun").setValue(1);

                        editTextHumidMax.setText("");
                        editTextHumidMin.setText("");


                        editTextHumidMax.setFocusable(false);
                        editTextHumidMin.setFocusable(false);

                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changeHumidMin() {
        editTextHumidMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTextHumidMin.onEditorAction(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(getContext())
                .setTitle("แก้ไข " + getString(R.string.humidity))
                .setMessage("ต้องการเปลี่ยน" + getString(R.string.minimum) + "จาก " + Integer.parseInt(editTextHumidMin.getHint().toString()) + " % เป็น " + Integer.parseInt(editTextHumidMin.getText().toString()) + " % ใช่หรือไม่"
                )
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbRef.child("humidMin").setValue(Integer.parseInt(editTextHumidMin.getText().toString()) + ".00 %");
                        editTextHumidMin.setHint(String.valueOf(Integer.parseInt(editTextHumidMin.getText().toString())));

                        dbRef.child("statushumidMin").setValue(1);
                        dbRef.child("statusrun").setValue(1);

                        editTextHumidMax.setText("");
                        editTextHumidMin.setText("");


                        editTextHumidMax.setFocusable(false);
                        editTextHumidMin.setFocusable(false);
                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changeTempMax() {
        editTextTempMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTextTempMin.onEditorAction(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(getContext())
                .setTitle("แก้ไข " + getString(R.string.temperature))
                .setMessage("ต้องการเปลี่ยน" + getString(R.string.maximum) + "จาก " + Integer.parseInt(editTextTempMax.getHint().toString()) + " °C เป็น " + Integer.parseInt(editTextTempMax.getText().toString()) + " °C ใช่หรือไม่"
                )
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbRef.child("tempMax").setValue(Integer.parseInt(editTextTempMax.getText().toString()) + ".00 °C");

                        editTextTempMax.setHint(String.valueOf(Integer.parseInt(editTextTempMax.getText().toString())));

                        dbRef.child("statustempMax").setValue(1);
                        dbRef.child("statusrun").setValue(1);

                        editTextTempMax.setText("");
                        editTextTempMin.setText("");


                        editTextTempMax.setFocusable(false);
                        editTextTempMin.setFocusable(false);

                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void changeTempMin() {
        editTextTempMax.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTextTempMin.onEditorAction(EditorInfo.IME_ACTION_DONE);
        new AlertDialog.Builder(getContext())
                .setTitle("แก้ไข " + getString(R.string.temperature))
                .setMessage("ต้องการเปลี่ยน" + getString(R.string.minimum) + "จาก " + Integer.parseInt(editTextTempMin.getHint().toString()) + " °C เป็น " + Integer.parseInt(editTextTempMin.getText().toString()) + " °C ใช่หรือไม่"
                )
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbRef.child("tempMin").setValue(Integer.parseInt(editTextTempMin.getText().toString()) + ".00 °C");

                        editTextTempMin.setHint(String.valueOf(Integer.parseInt(editTextTempMin.getText().toString())));

                        dbRef.child("statustempMin").setValue(1);
                        dbRef.child("statusrun").setValue(1);

                        editTextTempMax.setText("");
                        editTextTempMin.setText("");

                        editTextTempMax.setFocusable(false);
                        editTextTempMin.setFocusable(false);

                        Toasty.success(getContext(), "แก้ไขเรียบร้อย", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();

    }

    private Boolean checkMin(int max, int min) {
        if (min < max) {
            return true;
        }
        return false;
    }

    private Boolean checkMax(int max, int min) {

        if (max > min) {
            return true;
        }
        return false;
    }

    private void onSwitchChange() {
        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("status", "on");
                        c.setVisibility(View.VISIBLE);
                    } else {

                        setStatus("status", "off");
                        switch1.setChecked(false);
                        switch2.setChecked(false);
                        switch3.setChecked(false);
                        switch4.setChecked(false);
                        switch5.setChecked(false);
                        switch6.setChecked(false);
                        switch7.setChecked(false);
                        switch8.setChecked(false);
                        switch9.setChecked(false);
                        c.setVisibility(View.GONE);
                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }

            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("1", "on");

                    } else {

                        setStatus("1", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();

                }

            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("2", "on");

                    } else {

                        setStatus("2", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("3", "on");

                    } else {

                        setStatus("3", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("4", "on");

                    } else {

                        setStatus("4", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("5", "on");

                    } else {

                        setStatus("5", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("6", "on");

                    } else {

                        setStatus("6", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("7", "on");

                    } else {

                        setStatus("7", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("8", "on");

                    } else {

                        setStatus("8", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });

        switch9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isNetworkConnected()) {
                    if (isChecked) {

                        setStatus("9", "on");

                    } else {

                        setStatus("9", "off");

                    }
                } else {
                    countCheckOn += 1;
                    checkOn();
                }
            }
        });
    }

    private void checkOn() {
        if (countCheckOn > countOn) {
            Toasty.error(getContext(), "โปรดเชื่อมต่ออินเตอร์เน็ตก่อนใช้งาน", Toast.LENGTH_SHORT).show();
        }
    }

    private void setHint() {

        dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                editTextFillAir.setHint(String.valueOf(dataSnapshot.child("delayAir").getValue()));
                editTextFillWater.setHint(String.valueOf(dataSnapshot.child("delayWater").getValue()));
                editTextBinName.setHint(String.valueOf(dataSnapshot.child("binName").getValue()));

                String tempMax = String.valueOf(dataSnapshot.child("tempMax").getValue());
                String tempMin = String.valueOf(dataSnapshot.child("tempMin").getValue());
                String humidMax = String.valueOf(dataSnapshot.child("humidMax").getValue());
                String humidMin = String.valueOf(dataSnapshot.child("humidMin").getValue());

                String timeSensor = String.valueOf(dataSnapshot.child("timesensor").getValue());

                if (tempMax != null && tempMin != null && humidMax != null && humidMin != null) {
                    editTextTempMax.setHint(tempMax.substring(0, tempMax.indexOf(".")));
                    editTextTempMin.setHint(tempMin.substring(0, tempMin.indexOf(".")));
                    editTextHumidMax.setHint(humidMax.substring(0, humidMax.indexOf(".")));
                    editTextHumidMin.setHint(humidMin.substring(0, humidMin.indexOf(".")));
                }

                if (timeSensor != null) {
                    if (timeSensor.equals("20")) {
                        s.setSelection(2);
                    } else if (timeSensor.equals("10")) {
                        s.setSelection(1);
                    } else {
                        s.setSelection(0);
                    }
                }


                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setSwitch() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    String binPart = BinSnapshot.getKey();
                    Map map = (Map) BinSnapshot.getValue();
                    Log.d("notistaz", binPart);
                    final String bin = String.valueOf(map.get("binid"));
                    if (binID.equals(bin)) {
                        Log.d("notistaz", binID);
                        String notifyStatusAll = String.valueOf(BinSnapshot.child("notificationStatus").child("status").getValue());
                        String notifyStatus1 = String.valueOf(BinSnapshot.child("notificationStatus").child("1").getValue());
                        String notifyStatus2 = String.valueOf(BinSnapshot.child("notificationStatus").child("2").getValue());
                        String notifyStatus3 = String.valueOf(BinSnapshot.child("notificationStatus").child("3").getValue());
                        String notifyStatus4 = String.valueOf(BinSnapshot.child("notificationStatus").child("4").getValue());
                        String notifyStatus5 = String.valueOf(BinSnapshot.child("notificationStatus").child("5").getValue());
                        String notifyStatus6 = String.valueOf(BinSnapshot.child("notificationStatus").child("6").getValue());
                        String notifyStatus7 = String.valueOf(BinSnapshot.child("notificationStatus").child("7").getValue());
                        String notifyStatus8 = String.valueOf(BinSnapshot.child("notificationStatus").child("8").getValue());
                        String notifyStatus9 = String.valueOf(BinSnapshot.child("notificationStatus").child("9").getValue());


                        if (notifyStatusAll.equals("off") || (notifyStatus1.equals("off") && notifyStatus2.equals("off") && notifyStatus3.equals("off") && notifyStatus4.equals("off") && notifyStatus5.equals("off") && notifyStatus6.equals("off") && notifyStatus7.equals("off") && notifyStatus8.equals("off") && notifyStatus9.equals("off"))) {
                            Log.d("notista", notifyStatusAll);
                            dbRef.child(binPart).child("notificationStatus").child("status").setValue("off");
                            switchAll.setChecked(false);

                            for (int i = 1; i <= 9; i++) {
                                Log.d("notistaz", binPart);
                                dbRef.child(binPart).child("notificationStatus").child(String.valueOf(i)).setValue("off");
                            }
                            c.setVisibility(View.GONE);
                        } else {
                            switchAll.setChecked(true);
                            countOn += 1;
                            if (notifyStatus1.equals("on")) {
                                countOn += 1;
                                switch1.setChecked(true);
                            } else {
                                switch1.setChecked(false);
                            }

                            if (notifyStatus2.equals("on")) {
                                countOn += 1;
                                switch2.setChecked(true);
                            } else {
                                switch2.setChecked(false);
                            }

                            if (notifyStatus3.equals("on")) {
                                countOn += 1;
                                switch3.setChecked(true);
                            } else {
                                switch3.setChecked(false);
                            }

                            if (notifyStatus4.equals("on")) {
                                countOn += 1;
                                switch4.setChecked(true);
                            } else {
                                switch4.setChecked(false);
                            }
                            if (notifyStatus5.equals("on")) {
                                countOn += 1;
                                switch5.setChecked(true);
                            } else {
                                switch5.setChecked(false);
                            }
                            if (notifyStatus6.equals("on")) {
                                countOn += 1;
                                switch6.setChecked(true);
                            } else {
                                switch6.setChecked(false);
                            }
                            if (notifyStatus7.equals("on")) {
                                countOn += 1;
                                switch7.setChecked(true);
                            } else {
                                switch7.setChecked(false);
                            }
                            if (notifyStatus8.equals("on")) {
                                countOn += 1;
                                switch8.setChecked(true);
                            } else {
                                switch8.setChecked(false);
                            }
                            if (notifyStatus9.equals("on")) {
                                countOn += 1;
                                switch9.setChecked(true);
                            } else {
                                switch9.setChecked(false);
                            }
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

    private void setStatus(final String typeNotify, final String status) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users/" + auth.getCurrentUser().getUid() + "/bin");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot BinSnapshot : dataSnapshot.getChildren()) {
                    Map map = (Map) BinSnapshot.getValue();
                    final String binid = String.valueOf(map.get("binid"));
                    String binPart = BinSnapshot.getKey();
                    if (binid.equals(binID)) {

                        dbRef.child(binPart).child("notificationStatus").child(typeNotify).setValue(status);
                        if (typeNotify.equals("status") && status.equals("on")) {
                            // dbRef.child(binPart).child("notification").setValue("on");
                            Log.d("inni", "om");
                        } else if (typeNotify.equals("status") && status.equals("off")) {

                            // dbRef.child(binPart).child("notification").setValue("off");
                            for (int i = 1; i <= 9; i++) {
                                removeToken(binID, i + "");
                                dbRef.child(binPart).child("notificationStatus").child(String.valueOf(i)).setValue("off");
                            }
                        } else if (status.equals("on")) {
                            setUserToken(binID, typeNotify);
                        } else if (status.equals("off")) {
                            removeToken(binID, typeNotify);
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

    private void removeToken(String binID, String typeNotify) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(NODE_fcm + "/" + binID + "/" + typeNotify).child(token);
        dbRef.removeValue();

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

    @SuppressLint("ClickableViewAccessibility")
    private void hideKeyBord() {

        editTextBinName.setFocusable(false);
        editTextBinName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextBinName.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextFillWater.setFocusable(false);
        editTextFillWater.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextFillWater.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextFillAir.setFocusable(false);
        editTextFillAir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextFillAir.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextTempMin.setFocusable(false);
        editTextTempMin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextTempMin.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextTempMax.setFocusable(false);
        editTextTempMax.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextTempMax.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextHumidMin.setFocusable(false);
        editTextHumidMin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextHumidMin.setFocusableInTouchMode(true);

                return false;
            }
        });

        editTextHumidMax.setFocusable(false);
        editTextHumidMax.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editTextHumidMax.setFocusableInTouchMode(true);

                return false;
            }
        });
    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }

}
