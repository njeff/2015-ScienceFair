package com.sci2015fair.fileoperations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mitchell on 10/31/2015.
 */
public class ConsoleLogObject {
    long id;
    private String date;
    private String time;
    private String outputmessagecategory;
    private String mainoutputstring;

    public ConsoleLogObject(long id, String outputmessagecategory, String mainoutputstring) {
        this.id = id;
        this.date = setDate();
        this.time = setTime();
        this.outputmessagecategory = outputmessagecategory;
        this.mainoutputstring = mainoutputstring;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String setDate() {
        DateFormat tf = new SimpleDateFormat("MM/dd/yy");
        Date dateobj = new Date();
        date = tf.format(dateobj);
        return date;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String setTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date();
        time = df.format(dateobj);
        return time;
    }

    public String getOutputMessageCategory() {
        return outputmessagecategory;
    }

    public void setOutputMessageCategory(int categoryid) {
        String[] categorylist = new String[3];
        categorylist[1] = "ERROR";
        categorylist[2] = "CAMERA";
        categorylist[3] = "SAVE";

        outputmessagecategory = categorylist[categoryid];
    }

    public String getMainOutputString() {
        return mainoutputstring;
    }

    public void setMainOutputString(String mainoutputstring) {
        this.mainoutputstring = mainoutputstring;
    }


}
