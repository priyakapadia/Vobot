package com.wowwee.chip_android_sampleproject.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;
import com.wowwee.chip_android_sampleproject.utils.SQLHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class GraphFragment extends Fragment {

    private Spinner spinner1;
    double[] x= new double[100];
    double[] y= new double[100];
    GraphView graph;
    LineGraphSeries<DataPoint> series;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graphs, container, false);

        addListenerOnSpinnerItemSelection(view); //call spinner function

        graph = (GraphView) view.findViewById(R.id.graph);


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
//
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(35);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling

        graph.setTitle(String.valueOf(spinner1.getSelectedItem()));
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Number of Sessions");
        gridLabel.setVerticalAxisTitle("Scores");

        Button backButton = (Button)view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
            }
        });


        series = new LineGraphSeries<>(data());//<DataPoint>//(new


        graph.addSeries(series);

        return view;


    }

    public DataPoint[] data(){

        String phonenum = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString(  "LoginPhone", ""); //login phone number
        double [] scores = SQLHelper.getSessionScores(phonenum,String.valueOf(spinner1.getSelectedItem()));
        double number_sessions = 0;
        try {
            number_sessions = scores.length;
        }
        catch (NullPointerException e){
            Log.d("Failed", "fail");
            Toast.makeText(getActivity(),"Database is not connected. Could not retrieve data. Please check your internet connection.",Toast.LENGTH_SHORT).show();
        }

        Log.d("length",  Double.toString(number_sessions));
        DataPoint[] values = new DataPoint[(int)number_sessions];     //this has to be the size of sessions

        int counter = 1;
        for(int i=0;i< (int)number_sessions ;i++){
            x[i] = counter++;
            y[i] = scores[i];
        }

        for(int i=0;i<(int)number_sessions;i++){
            DataPoint v = new DataPoint(x[i],y[i]);
            values[i] = v;
        }

        return values;
    }

    public void change_graph (View view){

        graph.setTitle(String.valueOf(spinner1.getSelectedItem()));
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Number of sessions");
        gridLabel.setVerticalAxisTitle("Scores");
        graph.removeAllSeries();
        series = new LineGraphSeries<>(data());
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        graph.addSeries(series);

    }

    public void addListenerOnSpinnerItemSelection(View v) {
        spinner1 = (Spinner) v.findViewById(R.id.spinner_graph_word_choice);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_graph_word_choice, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Log.v("item", (String) parent.getItemAtPosition(position));
                //series.resetData(data());
                change_graph(view);
                //graph.removeAllSeries();
                data();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }



}