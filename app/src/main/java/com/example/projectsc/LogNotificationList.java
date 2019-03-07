package com.example.projectsc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LogNotificationList  extends ArrayAdapter<LogNotification> {

    private Context context;
    private List<LogNotification> notiList;
    LayoutInflater inflater;

    public LogNotificationList(Context context, List<LogNotification> notiList) {
        super(context, R.layout.list_noti_layout, notiList);
        this.context = context;
        this.notiList = notiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_noti_layout, null, true);
        }


        TextView tv_type = convertView.findViewById(R.id.tv_typeNoti_list);
        TextView tv_time = convertView.findViewById(R.id.tv_time_list);
        TextView tv_date = convertView.findViewById(R.id.tv_date_list);

        LogNotification logNotification = notiList.get(position);

        tv_type.setText(logNotification.getType());
        tv_time.setText(logNotification.getTime());
        tv_date.setText(logNotification.getDate());

        return convertView;
    }
}
