package com.sci2015fair.programlogic;

import com.sci2015fair.filecontrolcenter.SaveLocations;
import android.location.Location;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sci2015fair.fileoperations.LocationGPSLogCSVReader.readLocationCSVFile;

/**
 * Created by Mitchell on 12/29/2015.
 */
public class LocationGPSClusterCalculator {

//    public static void correlateClusters() {//
//
//    }
//
//    public static void findClusters() {
//        ArrayList<LocationIDObject> allLocationData = new ArrayList<>(readLocationCSVFile(SaveLocations.DFLocationGPSLogCSV));//load all location data recorded in locationgpslog.csv into a LocationIDObject array
//        String primerDate = allLocationData.get(0).getDate();
//        int initialDayIndex = 0;
//        int finalDayIndex = 0;
//        int i;
//        for (i = 0; i < allLocationData.size(); i++) {//finds first index with date specified in primerDate
//            if (allLocationData.get(i).getDate().equals(primerDate)) {
//                initialDayIndex = i;
//                break;
//            }
//        }
//
//        for ( ;i < allLocationData.size(); i++) {//finds first index of the day after the specified primerDate
//
//            if (allLocationData.get(i).getDate().equals(addOneDay(primerDate))) {
//                finalDayIndex = i;
//            }
//        }
//
//        LocationIDObject[] singleDayLocationData = new LocationIDObject[finalDayIndex - initialDayIndex + 1];//create an array of LocationIDObjects from just one day
//        Location[] locationCache = new Location[10];
//        int outOfClusterCount = 0;
////        double lat = 0;
////        double longt = 0;
//        double dist
//        for (int j = 0; j < singleDayLocationData.length; j++) {
////            lat = singleDayLocationData[i].getLocation().getLatitude();
////            longt = singleDayLocationData[i].getLocation().getLongitude();
//            for (int k = 1; k < locationCache.length; k++) {
//                locationCache[k] = locationCache[k - 1];
//            }
//            locationCache[0] = singleDayLocationData[j].getLocation();
//
//
//
//        }
//
//
//
//
//
//
//
//        (allLocationData.subList(initialDayIndex, finalDayIndex + 1)).toArray(singleDayLocationData);
//        LocationIDObject[][] singleDayInTimeBlocks = new LocationIDObject[12][];
//
//
//        String[] idTimeAnchors = {"0:00:00", "2:00:00", "4:00:00", "6:00:00", "8:00:00", "10:00:00", "12:00:00", "14:00:00", "16:00:00", "18:00:00", "20:00:00", "22:00:00"};//military time without separating colons (due to int form being easier to compare).  i.e. "20000" is 2:00:00 AM, etc.
//
//        String[] idTimeBounds = {"1:00:00", "3:00:00", "5:00:00", "7:00:00", "9:00:00", "11:00:00", "13:00:00", "15:00:00", "17:00:00", "19:00:00", "21:00:00", "23:00:00"};
//
//        long differenceToClosestTime = 0;
//        for (int i = 0; i < idTimeAnchors.length; i++) {
//
//
//
//
//
//
////            singleDayLocationData.indexOf()
//            for (int j = 0; j < singleDayLocationData.size(); i++) {
//                if (j == 0) {
//                    differenceToClosestTime = singleDayLocationData.get(j).getLocation().getTime();
//                } else if (differenceToClosestTime > singleDayLocationData.get(j).getLocation().getTime()) {
//                    differenceToClosestTime = singleDayLocationData.get(j).getLocation().getTime();
//                    indexOfClosestTime[i] = j;
//                }
//            }
//
//
//
//
//
//
//        }
//
//
//    }
//
//    public static void compareProximity() {
//
//    }
//
//    private static double distanceCalculator(LocationIDObject pointA, LocationIDObject pointB, String unit) {//Calculates the distance between two coordinates on the globe.  Depending on unit specified, returns a (double) answer in Miles(M), Kilometers(K), or Nautical Miles (N).
//        double lat1 = pointA.getLocation().getLatitude();
//        double lon1 = pointA.getLocation().getLongitude();
//        double lat2 = pointB.getLocation().getLatitude();
//        double lon2 = pointB.getLocation().getLongitude();
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        if (unit == "K") {
//            dist = dist * 1.609344;
//        } else if (unit == "N") {
//            dist = dist * 0.8684;
//        }
//
//        return (dist);
//    }
//
//    private static double deg2rad(double deg) {//Converts degrees to radians.
//        return (deg * Math.PI / 180.0);
//    }
//
//    private static double rad2deg(double rad) {//Converts radians to degrees.
//        return (rad * 180 / Math.PI);
//    }
//
//    public static boolean timeWithinHourCompare(String anchorTime, String testTime) {//Method takes testTime and compares it to anchorTime to see if it is within +-1 hour of it to return true,  false if otherwise.  Both parameters take time in the form of "HH:mm:ss".
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        cal1.setTime(sdf.parse(anchorTime));
//        cal2.setTime(sdf.parse(testTime));
//        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
//
//
//
//        Calendar calendar = Calendar.getInstance();
//        try {
//            calendar.setTime(sdf.parse(dateString));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        calendar.add(Calendar.DATE, 1);//second parameter is number of days to add
//        dateString = sdf.format(calendar.getTime());  // dateString is now the new date
//        return dateString;
//    }
//
//    public static long timeDifferenceCompare(String anchorTime, String testTime) {//Compares the absolute difference of testTime from anchorTime and returns an answer in (long) milliseconds.  Both parameters take time in the form of "HH:mm:ss".
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//set format of Date objects below to hours:min:sec
//        long timeDifference = 0;
//        try {//convert passed in Strings into Date objects for easier manipulation
//            Date anchorTimeDObject = sdf.parse(anchorTime);
//            Date testTimeDObject = sdf.parse(testTime);
//            timeDifference = Math.abs(anchorTimeDObject.getTime() - testTimeDObject.getTime());//get time difference in millseconds by subtracting the Unix Time count of both Dates
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return timeDifference;
//    }
//
//    public static String addOneDay(String dateString) {//Advances a date passed in via dateString by one day and returns it as a String.  Parameter must be in the form of "MM/dd/yy" (e.g. 12/31/1999, 3/15/2020, 8/8/2008).
//        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");//set format of calendar (below) to months/days/years
//        Calendar calendar = Calendar.getInstance();
//        try {
//            calendar.setTime(sdf.parse(dateString));//convert dateString into a calendar object for easy manipulation of date
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        calendar.add(Calendar.DATE, 1);//second parameter is number of days to add
//        dateString = sdf.format(calendar.getTime());//replace old date in dateString with new date generated via line above
//        return dateString;
//    }
}
