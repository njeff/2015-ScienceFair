package com.sci2015fair.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sci2015fair.R;
import com.sci2015fair.fileoperations.SurveyLogCSV;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SurveyActivity extends AppCompatActivity {

    private TextView tvDisplayDate;
    //private DatePicker dpResult;
    private Button btnChangeDate;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        //create date display and change options
        setCurrentDateOnView();//set textView to display current date
        addDateChangeListenerOnButton();//bind button to datePickerDialog for user to change date as he/she pleases
        //add spinners for questions #2 and #3
        addItemsOnSpinner(R.id.day_scale_spinner, R.array.day_scale_spinner_options);
        addItemsOnSpinner(R.id.hours_sleep_received_spinner, R.array.hours_sleep_received_spinner_options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // add items/choices into the spinner specified through the passed-in int targetSpinnerID
    public void addItemsOnSpinner(int targetSpinnerID, int resArray) {
        Spinner spinner = (Spinner) findViewById(targetSpinnerID);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                resArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *
     */
    // display current date
    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
//        dpResult = (DatePicker) findViewById(R.id.dpResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into datepicker
//        dpResult.init(year, month, day, null);

    }

    public void addDateChangeListenerOnButton() {

        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        btnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

//            // set selected date into datepicker also
//            dpResult.init(year, month, day, null);

        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onRadioButtonClicked(View view) {
//        // Is the button now checked?
//        boolean checked = ((RadioButton) view).isChecked();
//
//        // Check which radio button was clicked
//        switch(view.getId()) {
//            case R.id.radioButton1:
//            case R.id.radioButton2:
//            case R.id.radioButton3:
//            case R.id.radioButton4:
//            case R.id.radioButton5:
//            case R.id.radioButton6:
//                if (checked) {
//                }
//                break;
////
////            case R.id.:
////                if (checked)
////                    // Ninjas rule
////                    break;
//        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener onSubmitClick = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

        }
    };

    public void onSubmitClick(View arg0) {
        String entryForDay = dateToString();
        dateToString();
        Spinner generalScaleS = (Spinner)findViewById(R.id.day_scale_spinner);
        Spinner hourSleepS = (Spinner)findViewById(R.id.hours_sleep_received_spinner);
        String generalScale = generalScaleS.getSelectedItem().toString();
        String hoursOfSleep = hourSleepS.getSelectedItem().toString() + " Hrs";
        String mSleep = getRadioGroupSelectedIndex(R.id.radiogroup1);
        String mSad = getRadioGroupSelectedIndex(R.id.radiogroup2);
        String mNeutral = getRadioGroupSelectedIndex(R.id.radiogroup3);
        String mHappy = getRadioGroupSelectedIndex(R.id.radiogroup4);
        String mSurprised = getRadioGroupSelectedIndex(R.id.radiogroup5);
        System.out.println(generalScaleS);
        System.out.println(hoursOfSleep);
        System.out.println(mSleep);
        System.out.println(mSad);
        System.out.println(mNeutral);
        System.out.println(mHappy);
        System.out.println(mSurprised);



        if ( !mSleep.equals("-1") && !mSad.equals("-1") && !mNeutral.equals("-1") && !mHappy.equals("-1") && !mSurprised.equals("-1")) {
            SurveyLogCSV.writeNewEntry(entryForDay, generalScale, hoursOfSleep, mSleep, mSad, mNeutral, mHappy, mSurprised);
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        } else {
            Toast.makeText(SurveyActivity.this, "Not all fields completed.", Toast.LENGTH_SHORT).show();
        }

    }

    public String dateToString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return df.format(cal.getTime());
    }

    public String getRadioGroupSelectedIndex(int id) {//returns index of the selected radiobutton in a radiogroup starting from 0.
        RadioGroup radiogroup = (RadioGroup) findViewById(id);
        int selectedId = radiogroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        return Integer.toString(radiogroup.indexOfChild(radioButton));
    }

}
