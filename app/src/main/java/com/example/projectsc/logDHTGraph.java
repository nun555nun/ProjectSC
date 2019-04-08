package com.example.projectsc;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class logDHTGraph extends AppCompatActivity {
    LineChart mLineChart;

    ArrayList<Entry> dataTemp;
    ArrayList<Entry> dataHumid;
    private String binID;
    List<LogDHT> logDHTList;
    private String toDate;
    ArrayList<String> dataDateTime;
    private String date;
    private int type;
    ArrayList<String> rangeD;
    ArrayList<String> range;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_dhtgraph);

        Intent i = getIntent();
        logDHTList = new ArrayList<>();
        date = i.getStringExtra("date");
        toDate = i.getStringExtra("todate");
        type = i.getIntExtra("type", 0);
        binID = i.getStringExtra("binID");
        //Toasty.success(logDHTGraph.this, "แสดงกราฟวันที่ " + date + " ถึง " + toDate + " " + type + " " + binID, Toasty.LENGTH_SHORT).show();

        rangeD = new ArrayList<>();
        dataTemp = new ArrayList<>();
        dataHumid = new ArrayList<>();
        dataDateTime = new ArrayList<>();

        mLineChart = findViewById(R.id.line_chart);
        mLineChart.setNoDataText("ไม่พบข้อมูล");
        mLineChart.setNoDataTextColor(Color.BLACK);
        // mLineChart.setBackgroundResource(R.drawable.bg_new);


        mLineChart.setDrawGridBackground(true);//สีเทาข้างหลัง
        //mLineChart.setDragEnabled(false);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(Color.GREEN);
        mLineChart.setBorderWidth(2);//กรอบ

        Legend legend = mLineChart.getLegend();//แก้ไขแบบของชื่อเส้น
        legend.setEnabled(true);
        legend.setTextColor(Color.BLUE);
        legend.setTextSize(10);
        legend.setForm(Legend.LegendForm.LINE);//สัญลักณ์


        getRangeDate();
        mLineChart.setPinchZoom(true);
        mLineChart.setScaleYEnabled(false);

        /*mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float x = e.getX();
                float y = e.getY();

                Toast.makeText(logDHTGraph.this, dataDateTime.get((int) x) + System.getProperty("line.separator") + y, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });*/

    }


    private void getDataTemp() {

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (logDHTGraph.this == null) {
                    dbRef.removeEventListener(this);
                } else {
                    int i = 0;
                    if(toDate.equals("-")&&date.equals("-")){
                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);

                                logDHTList.add(logDHT);
                                String temp = logDHT.getTemperature();
                                float tempF = Float.parseFloat(temp.substring(0, temp.indexOf(" ")));

                                dataTemp.add(new Entry(i, tempF));
                                String humid = logDHT.getHumidity();
                                float humidF = Float.parseFloat(humid.substring(0, humid.indexOf(" ")));
                                if (humidF < 100) {
                                    dataHumid.add(new Entry(i, humidF));
                                }
                                dataDateTime.add(logDHT.getDate() + System.getProperty("line.separator") + logDHT.getTime());
                                i++;

                        }
                    }
                    else if (toDate.equals("-")&&!date.equals("-")) {
                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);

                            if (date.equals(logDHT.getDate())) {
                                logDHTList.add(logDHT);
                                String temp = logDHT.getTemperature();
                                float tempF = Float.parseFloat(temp.substring(0, temp.indexOf(" ")));

                                dataTemp.add(new Entry(i, tempF));
                                String humid = logDHT.getHumidity();
                                float humidF = Float.parseFloat(humid.substring(0, humid.indexOf(" ")));
                                if (humidF < 100) {
                                    dataHumid.add(new Entry(i, humidF));
                                }
                                dataDateTime.add(logDHT.getDate() + System.getProperty("line.separator") + logDHT.getTime());
                                i++;
                            }

                        }
                    } else if(!toDate.equals("-")&&!date.equals("-")){

                        for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                            LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                            for (int a = 0; a < rangeD.size(); a++) {
                                //Toasty.success(logDHTGraph.this, rangeD.get(a), Toasty.LENGTH_SHORT).show();
                                if (rangeD.get(a).equals(logDHT.getDate())) {
                                    logDHTList.add(logDHT);
                                    String temp = logDHT.getTemperature();
                                    float tempF = Float.parseFloat(temp.substring(0, temp.indexOf(" ")));

                                    dataTemp.add(new Entry(i, tempF));
                                    String humid = logDHT.getHumidity();
                                    float humidF = Float.parseFloat(humid.substring(0, humid.indexOf(" ")));
                                    if (humidF < 100) {
                                        dataHumid.add(new Entry(i, humidF));
                                    }
                                    dataDateTime.add(logDHT.getDate() + System.getProperty("line.separator") + logDHT.getTime());
                                    i++;
                                }
                            }
                        }
                    }

                    if (logDHTList.size() > 0 && getApplicationContext() != null) {

                        LineDataSet lineDataSet1 = new LineDataSet(dataTemp, "อุณหภูมิ");
                        LineDataSet lineDataSet2 = new LineDataSet(dataHumid, "ความชื้น");

                        lineDataSet1.setColor(Color.BLUE);
                        lineDataSet1.setLineWidth(2);
                        // lineDataSet1.setValueTextSize(10);
                        lineDataSet1.setCircleColor(Color.BLUE);
                        lineDataSet1.setCircleRadius(2);
                        lineDataSet1.setDrawCircleHole(true);
                        lineDataSet1.setCircleHoleColor(Color.BLUE);
                        lineDataSet1.setDrawValues(false);

                        lineDataSet2.setColor(Color.RED);
                        lineDataSet2.setLineWidth(2);
                       // lineDataSet2.setValueTextSize(10);
                        lineDataSet2.setCircleColor(Color.RED);
                        lineDataSet2.setCircleRadius(2);
                        lineDataSet2.setDrawCircleHole(true);
                        lineDataSet2.setCircleHoleColor(Color.RED);
                        lineDataSet2.setDrawValues(false);


                        lineDataSet1.setValueFormatter(new MyValueFormatter());
                        lineDataSet2.setValueFormatter(new MyValueFormatter2());

                        lineDataSet1.setHighlightLineWidth(2);
                        lineDataSet2.setHighlightLineWidth(2);

                        XAxis xAxis = mLineChart.getXAxis();
                        YAxis yAxisL = mLineChart.getAxisLeft();
                        YAxis yAxisR = mLineChart.getAxisRight();

                        xAxis.setValueFormatter(new MyXAxisValueFormatter(dataDateTime));

//xAxis.setLabelRotationAngle(355);

                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        Description description = new Description();

                        description.setTextColor(Color.BLACK);
                        description.setTextSize(10);
                        String time = "";

                        if(!date.equals("-")){
                            if (toDate.equals("-")) {
                                time = "ช่วงวันที่ " + date;
                            } else {
                                time = "ช่วงวันที่ " + date + " ถึง " + toDate;
                            }
                        }
                        if (type == 0) {
                            dataSets.add(lineDataSet1);
                            dataSets.add(lineDataSet2);
                            yAxisL.setValueFormatter(new MyValueFormatter());
                            yAxisR.setValueFormatter(new MyValueFormatter2());
                            description.setText("กราฟแสดงความชื้นและอุณหภูมิ " + time);

                        } else if (type == 1) {
                            dataSets.add(lineDataSet1);
                            yAxisL.setValueFormatter(new MyValueFormatter());
                            yAxisR.setValueFormatter(new MyValueFormatter());
                            description.setText("กราฟแสดงอุณหภูมิ " + time);
                        } else if (type == 2) {
                            dataSets.add(lineDataSet2);
                            yAxisL.setValueFormatter(new MyValueFormatter2());
                            yAxisR.setValueFormatter(new MyValueFormatter2());
                            description.setText("กราฟแสดงความชื้น " + time);
                        }
                        mLineChart.setDescription(description);//ข้อความข้างล่าง


                        LineData data = new LineData(dataSets);
                        mLineChart.setData(data);
                        if (logDHTList.size() > 500) {
                            mLineChart.animateX(3500, Easing.EaseInSine);
                        } else {
                            mLineChart.animateX(2500, Easing.EaseInSine);
                        }

                        mLineChart.invalidate();

                        MyMarkerView mv = new MyMarkerView(logDHTGraph.this, R.layout.marker_layout, dataDateTime, type);
                        mLineChart.setMarker(mv);

                    }
                }

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRangeDate() {
        range = new ArrayList<>();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bin/" + binID + "/logDHT");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot logDHTSnapshot : dataSnapshot.getChildren()) {
                    LogDHT logDHT = logDHTSnapshot.getValue(LogDHT.class);
                    String dateString = logDHT.getDate();
                    if (range.size() == 0) {
                        range.add(dateString);
                    } else {
                        if (!range.get(range.size() - 1).equals(dateString)) {
                            range.add(dateString);
                        }
                    }

                }
                dbRef.removeEventListener(this);
                if (range.size() > 0) {

                    //Toasty.success(logDHTGraph.this, String.valueOf(range.size()), Toasty.LENGTH_SHORT).show();
                    boolean b = false;
                    for (int i = 0; i < range.size(); i++) {
                        if (toDate.equals("-")) {
                            break;
                        }
                        if (range.get(i).equals(date)) {
                            b = true;
                        }
                        if (b) {
                            rangeD.add(range.get(i));
                        }
                        if (range.get(i).equals(toDate)) {
                            break;
                        }
                    }
                    getDataTemp();
                    // Toasty.success(logDHTGraph.this, String.valueOf(rangeD.size()), Toasty.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private class MyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value) + " °C";
        }

    }

    private class MyValueFormatter2 extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return super.getFormattedValue(value) + " %";
        }

    }

    public class MyXAxisValueFormatter extends ValueFormatter {

        private ArrayList<String> mValues;

        public MyXAxisValueFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value) {
            return mValues.get((int) value);
        }
    }


}
