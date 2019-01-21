package com.example.projectsc;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        String position = getArguments().getString("binID");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView temptv = view.findViewById(R.id.temp);
        temptv.setText(position + "°C");
        //hide keybord
        final EditText editWater = view.findViewById(R.id.editWater);
        editWater.setFocusable(false);

        editWater.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editWater.setFocusableInTouchMode(true);

                return false;
            }
        });
        //hide keybord
        final EditText editAir = view.findViewById(R.id.editAir);
        editAir.setFocusable(false);
        editAir.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editAir.setFocusableInTouchMode(true);

                return false;
            }
        });

        Button fillWater = view.findViewById(R.id.button_water);
        fillWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editWater.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String time = editWater.getText().toString();
                if (time.length() == 0 || time.startsWith("0")) {
                    Toast.makeText(getContext(), "กรุณากรอกตัวเลขตั้งแต่1ขึ้นไป", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการตั้งเวลาเติมน้ำเป็นเวลา " + time + " นาที ใช่หรือไม่?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("no", null)
                            .show();
                }
            }
        });


        Button fillAir = view.findViewById(R.id.button_air);

        fillAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = editAir.getText().toString();
                if (time.length() == 0 || time.startsWith("0")) {
                    Toast.makeText(getContext(), "กรุณากรอกตัวเลขตั้งแต่1ขึ้นไป", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("ต้องการตั้งเวลาเติมอากาศเป็นเวลา " + time + " นาที ใช่หรือไม่?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("no", null)
                            .show();
                }
            }
        });

        return view;
    }

}
