package com.sci2015fair.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.sci2015fair.R;
import com.sci2015fair.filecontrolcenter.SaveLocations;
import com.sci2015fair.fileoperations.ReverseLineInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * TODO:
 * - combine graphs? (will tie into overall mood)
 */
public class StatsSummaryFragment extends Fragment {
    private static String TAG = "Stats";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsSummaryFragment newInstance(String param1, String param2) {
        StatsSummaryFragment fragment = new StatsSummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StatsSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats_summary, container, false);
        XYPlot plot = (XYPlot)view.findViewById(R.id.plot);

        int samples = 100;
        ArrayList<ArrayList<Number>> emotion = new ArrayList<ArrayList<Number>>();
        for(int i = 0; i<5; i++)
            emotion.add(new ArrayList<Number>());
        Number dist[] = {0,0,0,0,0,0,0};
        try{
            //read last week of the distance log
            String oldestdate = "";
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(SaveLocations.TotalDistanceLog)));
            String line;
            int i = 7-1; //one week's data
            while ((line = fileReader.readLine()) != null) {
                String[] s = line.split(",");
                if(!s[0].equals("Date")){
                    dist[i] = Math.log10(Math.abs(Double.parseDouble(s[1])))/2.0; //get the delta or dist
                    oldestdate = s[0]; //record oldest date
                }
                i--;
                if(i<0)
                    break;
            }
            fileReader.close();
            Log.d(TAG,oldestdate);
            DateFormat dfm = new SimpleDateFormat("MM/dd/yy");
            long oldtime = dfm.parse(oldestdate).getTime();

            //read expression log backwards up to last distance log
            fileReader = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(SaveLocations.expressionCSV)));
            i = samples-1;
            long currentUT = 0;
            while ((line = fileReader.readLine()) != null) {
                String[] s = line.split(",");
                if(!s[0].equals("ID")){
                    currentUT = dfm.parse(s[1]).getTime(); //unix time * 1000
                    for(int j = 0; j<5; j++)
                        emotion.get(j).add(Double.parseDouble(s[s.length-5+j]));
                }
                i--;
                if(i<0||currentUT<oldtime) //read values up to the oldest date from the distance log
                    break;
            }
            fileReader.close();
            for(int q = 0; q<5; q++) //flip around so most recent values on on the right of graph
                Collections.reverse(emotion.get(q));

            //colors
            int[] rainbow = getContext().getResources().getIntArray(R.array.rainbow);

            //averaged line
            Number[] avgE = new Number[emotion.get(0).size()];
            for(int j =0; j<emotion.get(0).size(); j++){ //weighted average line
                avgE[j] = emotion.get(0).get(j).doubleValue()*0.9
                        +emotion.get(1).get(j).doubleValue()*0.5
                        +emotion.get(2).get(j).doubleValue()*0.1
                        +emotion.get(3).get(j).doubleValue()*0.2
                        +emotion.get(4).get(j).doubleValue()*1;
            }
            SimpleXYSeries avg = new SimpleXYSeries(Arrays.asList(avgE),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Average");
            LineAndPointFormatter lf = new LineAndPointFormatter(rainbow[0], // line color
                    rainbow[0], // point color
                    Color.argb(0,0,0,0), // fill
                    null);
            lf.setInterpolationParams( //smoothing
                    new CatmullRomInterpolator.Params(7, CatmullRomInterpolator.Type.Centripetal));
            plot.addSeries(avg,lf);

            //movement line
            Number[] xaxis = new Number[7];
            for(int q = 0; q<7; q++)
                xaxis[q] = (double)(q*emotion.get(0).size())/7.0;

            SimpleXYSeries movement = new SimpleXYSeries(Arrays.asList(xaxis),
                    Arrays.asList(dist), "Distance");
            LineAndPointFormatter lf2 = new LineAndPointFormatter(rainbow[1], // line color
                    rainbow[1], // point color
                    Color.argb(0,0,0,0), // fill
                    null);
            lf2.setInterpolationParams( //smoothing
                    new CatmullRomInterpolator.Params(7, CatmullRomInterpolator.Type.Centripetal));
            plot.addSeries(movement, lf2);

            plot.getLegendWidget().setTableModel(new DynamicTableModel(2, 3));
            plot.setTicksPerRangeLabel(10);
        } catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}