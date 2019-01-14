package com.example.projectsc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LogDHTList extends ArrayAdapter<LogDHT> {

    private Context context;
    private List<LogDHT> dhtList;
    LayoutInflater inflater;

    public LogDHTList(Context context, List<LogDHT> dhtList) {
        super(context, R.layout.log_list_layout, dhtList);
        this.context = context;
        this.dhtList = dhtList;
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

        LogDHT logDHT = dhtList.get(position);

        tv_temp.setText(logDHT.getTemperature());
        tv_humid.setText(logDHT.getHumidity());
        tv_time.setText(logDHT.getTime());
        tv_date.setText(logDHT.getDate());

        return convertView;
    }
}
