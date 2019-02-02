package com.example.projectsc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserBinList extends ArrayAdapter<UserBin> {

    private Context context;
    private List<UserBin> binList;
    LayoutInflater inflater;

    public UserBinList(Context context, List<UserBin> binList) {
        super(context, R.layout.list_layout_bin, binList);
        this.context = context;
        this.binList = binList;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_layout_bin, null, true);
        }

        TextView tv_name = convertView.findViewById(R.id.bin_name_text_view);
        TextView tv_id = convertView.findViewById(R.id.bin_id_text_view);
        TextView tv_temp = convertView.findViewById(R.id.temp_text_view);
        TextView tv_humid = convertView.findViewById(R.id.humid_text_view);
        TextView tv_start = convertView.findViewById(R.id.start_text_view);


        UserBin bin = binList.get(position);
        tv_name.setText(bin.getBinName());
        tv_id.setText(bin.getBinID());
        tv_temp.setText(bin.getTemperature());
        tv_humid.setText(bin.getHumidity());
        tv_start.setText(bin.getDate());

        return convertView;
    }

}
