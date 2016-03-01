package com.sci2015fair.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.sci2015fair.R;
import com.sci2015fair.filecontrolcenter.SaveLocations;
import com.sci2015fair.fileoperations.ReverseLineInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConsoleLogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConsoleLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsoleLogFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "ConsoleLogF";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsoleLogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsoleLogFragment newInstance(String param1, String param2) {
        ConsoleLogFragment fragment = new ConsoleLogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ConsoleLogFragment() {
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
        View view = inflater.inflate(R.layout.fragment_console_log, container, false);
        TableLayout t = (TableLayout)view.findViewById(R.id.logtable);
        t.removeAllViews();
        try{
            //read last 25 lines of the log
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(SaveLocations.expressionCSV)));
            int i = 0;
            String line;
            TableRow.LayoutParams param = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT);
            param.weight = 1;
            while ((line = fileReader.readLine()) != null) {
                String[] s = line.split(",");
                TableRow tr = new TableRow(getContext());
                tr.setId(i);
                tr.setLayoutParams(param);

                for(int j = 0; j<4; j++){
                    TextView tv = new TextView(getContext());
                    tv.setId(j*2);
                    tv.setText(s[j]);
                    //Log.d(TAG,s[j]);
                    tv.setTextColor(Color.WHITE);
                    tv.setLayoutParams(param);
                    tr.addView(tv);
                }
                //Log.d(TAG,line);
                t.addView(tr);

                i++;
                if(i==25){
                    break;
                }
            }
            fileReader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onConsoleLogFragmentInteraction(uri);
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
        public void onConsoleLogFragmentInteraction(Uri uri);
    }

}
