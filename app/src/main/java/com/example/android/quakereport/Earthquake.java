package com.example.android.quakereport;


public class Earthquake {
    private String mMagnitude;
    private String mLocation;
    private String mDate;

    /**
     *
     * @param magnitude is the magnitude of the earthquake
     * @param location is the city location of the earthquake
     * @param date is the date the earthquake happened
     */
    public Earthquake(String magnitude, String location, String date){
        mMagnitude = magnitude;
        mLocation = location;
        mDate = date;
    }

    /**
     *
     * @return Returns the magnitude of the earthquake
     */
    public String getMagnitude(){
        return mMagnitude;
    }

    /**
     *
     * @return Returns the location of the earthquake
     */
    public String getLocation(){
        return mLocation;
    }

    /**
     *
     * @return Returns the date of the earthquake
     */
    public String getDate(){
        return mDate;
    }
}
