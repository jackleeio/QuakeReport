package com.example.android.quakereport;


public class Earthquake {
    private Double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;

    /**
     *
     * @param magnitude is the magnitude of the earthquake
     * @param location is the city location of the earthquake
     * @param timeInMilliseconds is the date the earthquake happened
     */
    public Earthquake(Double magnitude, String location, long timeInMilliseconds){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
    }

    /**
     *
     * @return Returns the magnitude of the earthquake
     */
    public Double getMagnitude(){
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
    public long getTimeInMilliseconds(){
        return mTimeInMilliseconds;
    }


}
