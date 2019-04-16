package com.example.projectsc;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private TextView tv;
    private ArrayList<String> str;
    private int type;

    public MyMarkerView(Context context, int layoutResource, ArrayList<String> s, int i) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
        tv =  findViewById(R.id.textView);
        str= s;
        type=i;
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String a = "";

        if(type==0){
            if(highlight.getDataSetIndex()==0){
                a=" °C";
            }else {
                a=" %";
            }
        }
        else if(type==1){
            a=" °C";
        }else if(type==2){
            a=" %";
        }
        tvContent.setText(str.get((int) e.getX()));
        tv.setText( e.getY()+a);


        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
