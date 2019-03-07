package com.example.projectsc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LogAllbinNotificationList extends ArrayAdapter<LogAllbinNotification> {

    private Context context;
    private List<LogAllbinNotification> notiList;
    LayoutInflater inflater;

    public LogAllbinNotificationList(Context context, List<LogAllbinNotification> notiList) {
        super(context, R.layout.list_all_noti_layout, notiList);
        this.context = context;
        this.notiList = notiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_all_noti_layout, null, true);
        }


        TextView tv_type = convertView.findViewById(R.id.tv_noti);
        TextView tv_time = convertView.findViewById(R.id.tv_time);
        TextView tv_date = convertView.findViewById(R.id.tv_date);
        TextView tv_binId = convertView.findViewById(R.id.tv_binID);
        TextView tv_binName = convertView.findViewById(R.id.tv_binName);
        ImageView iv_type = convertView.findViewById(R.id.iv_type);
        LogAllbinNotification logNotification = notiList.get(position);

        tv_type.setText(logNotification.getType());
        tv_time.setText(logNotification.getTime());
        tv_date.setText(logNotification.getDate());
        tv_binId.setText(logNotification.getBinID());
        tv_binName.setText("( "+logNotification.getBinName()+" )");

        if(logNotification.getType().equals("อุณหภูมิน้อยกว่าที่กำหนด")||logNotification.getType().equals("ความชื้นน้อยกว่าที่กำหนด")){
            tv_type.setTextColor(Color.parseColor("#33B4E4"));
            if(logNotification.getType().equals("ความชื้นน้อยกว่าที่กำหนด")){
                iv_type.setImageResource(R.drawable.humiditynew);
                // iv_type.setBackgroundColor(Color.parseColor("#4169E1"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_blue);
            }
            else {
                iv_type.setImageResource(R.drawable.thermometernew);
                // iv_type.setBackgroundColor(Color.parseColor("#4169E1"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_blue);
            }
        }
        else if(logNotification.getType().equals("อุณหภูมิมากกว่าที่กำหนด")||logNotification.getType().equals("ความชื้นมากกว่าที่กำหนด")){
            tv_type.setTextColor(Color.RED);
            if(logNotification.getType().equals("ความชื้นมากกว่าที่กำหนด")){
                iv_type.setImageResource(R.drawable.humiditynew);
                // iv_type.setBackgroundColor(Color.RED);
                //iv_type.setBackgroundColor(Color.TRANSPARENT);
                iv_type.setBackgroundResource(R.drawable.circle_bg_red);
            }
            else {
                iv_type.setImageResource(R.drawable.thermometernew);
                // iv_type.setBackgroundColor(Color.RED);
                iv_type.setBackgroundResource(R.drawable.circle_bg_red);
            }
        }
        else if(logNotification.getType().equals("เริ่มทำการเติมน้ำ")||logNotification.getType().equals("เริ่มทำการเติมอากาศ")){
            tv_type.setTextColor(Color.parseColor("#FF9933"));
            if(logNotification.getType().equals("เริ่มทำการเติมน้ำ")){
                iv_type.setImageResource(R.drawable.add_water);
                //  iv_type.setBackgroundColor(Color.parseColor("#FF9933"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_yellow);
            }
            else {
                iv_type.setImageResource(R.drawable.add_air);
                //  iv_type.setBackgroundColor(Color.parseColor("#FF9933"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_yellow);
            }

        }
        else if(logNotification.getType().equals("การเติมน้ำเสร็จเรียบร้อย")||logNotification.getType().equals("การเติมอากาศเสร็จเรียบร้อย")){
            tv_type.setTextColor(Color.parseColor("#97CA02"));
            if(logNotification.getType().equals("การเติมน้ำเสร็จเรียบร้อย")){
                iv_type.setImageResource(R.drawable.add_water);
                //  iv_type.setBackgroundColor(Color.parseColor("#32CD32"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_green);
            }
            else {
                iv_type.setImageResource(R.drawable.add_air);
                // iv_type.setBackgroundColor(Color.parseColor("#32CD32"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_green);
            }
        }
        return convertView;
    }
}

