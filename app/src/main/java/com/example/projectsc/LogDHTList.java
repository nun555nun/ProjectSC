package com.example.projectsc;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LogDHTList extends ArrayAdapter<LogDHT> {

    private Context context;
    private List<LogDHT> dhtList;
    LayoutInflater inflater;
    private int type;
    private String tempMax, tempMin, humidMax, humidMin;

    public LogDHTList(Context context, List<LogDHT> dhtList, int type, String tempMax, String tempMin, String humidMax, String humidMin) {
        super(context, R.layout.log_list_layout, dhtList);
        this.context = context;
        this.dhtList = dhtList;
        this.type = type;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.humidMax = humidMax;
        this.humidMin = humidMin;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.log_list_layout, null, true);
        }


        TextView tv_temp = convertView.findViewById(R.id.textView_temp_list);
        TextView tv_humid = convertView.findViewById(R.id.textView_humid_list);
        TextView tv_time = convertView.findViewById(R.id.textView_time_list);
        TextView tv_date = convertView.findViewById(R.id.textView_date_list);
        ConstraintLayout c = convertView.findViewById(R.id.cl_logDHT);
        LogDHT logDHT = dhtList.get(position);

        tv_temp.setText(logDHT.getTemperature());
        tv_humid.setText(logDHT.getHumidity());
        tv_time.setText(logDHT.getTime());
        tv_date.setText(logDHT.getDate());
        if (type == 0) {
            tv_date.setVisibility(View.VISIBLE);
        } else {
            tv_date.setVisibility(View.GONE);
        }
int count =0;
        //Toast.makeText(getContext(),tempMax+"  "+tempMin,Toast.LENGTH_SHORT).show();
        if (!tempMax.equals(tempMin)) {
            if (logDHT.getTemperature().equals(tempMax)) {
                tv_temp.setTextColor(Color.parseColor("#ffcc0000"));

                count++;
            } else if (logDHT.getTemperature().equals(tempMin)) {
                tv_temp.setTextColor(Color.parseColor("#FF74BC59"));

                count++;
            }else {
                tv_temp.setTextColor(Color.GRAY);

            }
        }else {
            tv_temp.setTextColor(Color.GRAY);

        }

        if (!humidMax.equals(humidMin)) {
            if (logDHT.getHumidity().equals(humidMax)) {
                tv_humid.setTextColor(Color.parseColor("#ffcc0000"));

                count++;
            } else if (logDHT.getHumidity().equals(humidMin)) {
                tv_humid.setTextColor(Color.parseColor("#FF74BC59"));

                count++;
            }else {
                tv_humid.setTextColor(Color.GRAY);

            }
        }else {
            tv_humid.setTextColor(Color.GRAY);

        }

        if(count>0){
            c.setBackgroundColor(Color.parseColor("#C8F7ED7A"));
        }else {
            c.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        return convertView;
    }

   /* @Override
    public boolean isEnabled(int position) {
        return false;
    }*/
}
