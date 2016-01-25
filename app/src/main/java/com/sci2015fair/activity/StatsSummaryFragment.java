package com.sci2015fair.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsSummaryFragment extends Fragment {
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
        int samples = 20;
        Number numbers[][] = new Number[5][samples];
        try{
            //read first 20 lines of the log
            BufferedReader fileReader = new BufferedReader(new FileReader(SaveLocations.expressionCSV));
            int i = 0;
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] s = line.split(",");
                if(i!=0){ //skip first line
                    for(int j = 0; j<5; j++){
                        numbers[j][i-1] = Double.parseDouble(s[s.length-5+j]);
                    }
                }

                i++;
                if(i>samples){
                    break;
                }
            }

            int[] rainbow = getContext().getResources().getIntArray(R.array.rainbow);

            fileReader.close();
            XYSeries[] xys = new XYSeries[5];
            String[] label = {"Happy","Neutral","Sad","Sleepy","Surprised"};
            LineAndPointFormatter[] lpf = new LineAndPointFormatter[5];
            for(int j =0; j<xys.length; j++){
                xys[j] = new SimpleXYSeries(Arrays.asList(numbers[j]),
                        SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, label[j]);
                lpf[j] = new LineAndPointFormatter(
                        rainbow[j], // line color
                        rainbow[j], // point color
                        Color.argb(0,0,0,0), // fill
                        null);
                Paint lineFill = new Paint();
                lineFill.setAlpha(0);
                lpf[j].setInterpolationParams(
                        new CatmullRomInterpolator.Params(7, CatmullRomInterpolator.Type.Centripetal));
                plot.addSeries(xys[j], lpf[j]);
            }
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