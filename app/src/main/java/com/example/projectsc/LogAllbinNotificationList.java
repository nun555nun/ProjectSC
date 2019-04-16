package com.example.projectsc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogAllbinNotificationList extends ArrayAdapter<LogAllbinNotification> {

    private Context context;
    private List<LogAllbinNotification> notiList;
    LayoutInflater inflater;
    private String lastSeen;
    private String currentTime;

    public LogAllbinNotificationList(Context context, List<LogAllbinNotification> notiList, String lastSeen) {
        super(context, R.layout.list_all_noti_layout, notiList);
        this.context = context;
        this.notiList = notiList;
        this.lastSeen = lastSeen;
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
        ConstraintLayout c = convertView.findViewById(R.id.constraintLayout4);

        tv_type.setText(logNotification.getType());
        tv_time.setText(logNotification.getTime());
        tv_date.setText(logNotification.getDate());
        tv_binId.setText(logNotification.getBinID());
        tv_binName.setText("( " + logNotification.getBinName() + " )");

        String notiTime = logNotification.getDate() + " " + logNotification.getTime();
        if (logNotification.getType().equals("อุณหภูมิน้อยกว่าที่กำหนด") || logNotification.getType().equals("ความชื้นน้อยกว่าที่กำหนด")) {
            tv_type.setTextColor(Color.parseColor("#33B4E4"));
            if (logNotification.getType().equals("ความชื้นน้อยกว่าที่กำหนด")) {
                iv_type.setImageResource(R.drawable.humiditynew);
                // iv_type.setBackgroundColor(Color.parseColor("#4169E1"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_blue);
            } else {
                iv_type.setImageResource(R.drawable.thermometernew);
                // iv_type.setBackgroundColor(Color.parseColor("#4169E1"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_blue);
            }
        } else if (logNotification.getType().equals("อุณหภูมิมากกว่าที่กำหนด") || logNotification.getType().equals("ความชื้นมากกว่าที่กำหนด")) {
            tv_type.setTextColor(Color.RED);
            if (logNotification.getType().equals("ความชื้นมากกว่าที่กำหนด")) {
                iv_type.setImageResource(R.drawable.humiditynew);
                // iv_type.setBackgroundColor(Color.RED);
                //iv_type.setBackgroundColor(Color.TRANSPARENT);
                iv_type.setBackgroundResource(R.drawable.circle_bg_red);
            } else {
                iv_type.setImageResource(R.drawable.thermometernew);
                // iv_type.setBackgroundColor(Color.RED);
                iv_type.setBackgroundResource(R.drawable.circle_bg_red);
            }
        } else if (logNotification.getType().equals("เริ่มทำการเติมน้ำ") || logNotification.getType().equals("เริ่มทำการเติมอากาศ")) {
            tv_type.setTextColor(Color.parseColor("#FF9933"));
            if (logNotification.getType().equals("เริ่มทำการเติมน้ำ")) {
                iv_type.setImageResource(R.drawable.add_water);
                //  iv_type.setBackgroundColor(Color.parseColor("#FF9933"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_yellow);
            } else {
                iv_type.setImageResource(R.drawable.add_air);
                //  iv_type.setBackgroundColor(Color.parseColor("#FF9933"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_yellow);
            }

        } else if (logNotification.getType().equals("การเติมน้ำเสร็จเรียบร้อย") || logNotification.getType().equals("การเติมอากาศเสร็จเรียบร้อย")) {
            tv_type.setTextColor(Color.parseColor("#97CA02"));
            if (logNotification.getType().equals("การเติมน้ำเสร็จเรียบร้อย")) {
                iv_type.setImageResource(R.drawable.add_water);
                //  iv_type.setBackgroundColor(Color.parseColor("#32CD32"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_green);
            } else {
                iv_type.setImageResource(R.drawable.add_air);
                // iv_type.setBackgroundColor(Color.parseColor("#32CD32"));
                iv_type.setBackgroundResource(R.drawable.circle_bg_green);
            }
        }else if (logNotification.getType().equals("เซนเซอร์มีปัญหา")) {
            tv_type.setTextColor(Color.RED);
                iv_type.setImageResource(R.drawable.ic_warning_24dp);
                //  iv_type.setBackgroundColor(Color.parseColor("#32CD32"));
            iv_type.setBackgroundResource(R.drawable.circle_bg_red);

        }


        if (dateDiff(lastSeen, logNotification.getDate(), logNotification.getTime())) {
            c.setBackgroundResource(R.drawable.bg_block);
        } else {
            c.setBackgroundResource(R.drawable.bg_block_gray);
        }
        Log.d("time", lastSeen.substring(0, lastSeen.indexOf(" ")) + "----------" + lastSeen.substring(lastSeen.indexOf(" ") + 1));
        return convertView;
    }

    public boolean dateDiff(String startDate, String notiDate, String notiTime) {


        String lastSeen = startDate;

        String lsD = startDate.substring(0, startDate.indexOf(" "));

        int lsDY = Integer.parseInt(lsD.substring(lsD.indexOf("/", 3) + 1));
        int lsDM = Integer.parseInt(lsD.substring(lsD.indexOf("/") + 1, lsD.indexOf("/", 3)));
        int lsDD = Integer.parseInt(lsD.substring(0, lsD.indexOf("/")));

        String lst = startDate.substring(startDate.indexOf(" ") + 1);
        int lsDH = Integer.parseInt(lst.substring(0,lst.indexOf(":")));
        int lsDm = Integer.parseInt(lst.substring(lst.indexOf(":")+1,lst.indexOf(":", 3)));
        int lsDs = Integer.parseInt(lst.substring(lst.indexOf(":", 3)+1));

        int ntDY = Integer.parseInt(notiDate.substring(notiDate.indexOf("/", 3) + 1));
        int ntDM = Integer.parseInt(notiDate.substring(notiDate.indexOf("/") + 1, notiDate.indexOf("/", 3)));
        int ntDD = Integer.parseInt(notiDate.substring(0, notiDate.indexOf("/")));

        int ntDH = Integer.parseInt(notiTime.substring(0,notiTime.indexOf(":")));
        int ntDm = Integer.parseInt(notiTime.substring(notiTime.indexOf(":")+1,notiTime.indexOf(":", 3)));
        int ntDs = Integer.parseInt(notiTime.substring(notiTime.indexOf(":", 3)+1));

        if (ntDY < lsDY) {
            return false;
        } else if (ntDY == lsDY) {
            if (ntDM < lsDM) {
                return false;
            } else if (ntDM == lsDM) {
                if (ntDD < lsDD) {
                    return false;
                } else if (ntDD == lsDD) {
                   if(ntDH<lsDH){
                       return false;
                   }else if(ntDH==lsDH){
                       if(ntDm<lsDm){
                           return false;
                       }else if(ntDm<lsDm){
                           if(ntDs<lsDs){
                               return false;
                           }else {
                               return true;
                           }
                       }else {
                           return true;
                       }
                   }else {
                       return true;
                   }

                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    public int dateDiff2(String endDate) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        try {

            Date enddate = df.parse(endDate);

            long diff = enddate.getTime();

            int dayDiff = (int) diff;

            return dayDiff;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public int timeDiff(String endDate) {

        DateFormat df = new SimpleDateFormat("HH:mm:ss");

        try {

            Date enddate = df.parse(endDate);

            long diff = enddate.getTime();

            int dayDiff = (int) diff;

            return dayDiff;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

}

