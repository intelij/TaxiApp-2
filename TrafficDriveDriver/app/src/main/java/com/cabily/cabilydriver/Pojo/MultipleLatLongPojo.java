package com.cabily.cabilydriver.Pojo;

/**
 * Created by user88 on 7/13/2017.
 */

public class MultipleLatLongPojo {
    private String Lat,lon;
    private String txt;

    public String getLon() {
        return this.lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return this.Lat;
    }

    public void setLat(String lat) {
        this.Lat = lat;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTxt() {
        return txt;
    }
}
