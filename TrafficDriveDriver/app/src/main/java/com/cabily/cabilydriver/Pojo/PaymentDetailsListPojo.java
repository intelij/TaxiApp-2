package com.cabily.cabilydriver.Pojo;

import com.cabily.cabilydriver.ArrivedTrip;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by user14 on 9/24/2015.
 */
public class PaymentDetailsListPojo {
    private String ride_id;
    private String amount;
    private String ride_date;
    private JSONArray sample;

    public String getride_id() {
        return ride_id;
    }

    public void setride_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getamount() {
        return amount;
    }

    public void setamount(String amount) {
        this.amount = amount;
    }

    public String getride_date() {
        return ride_date;
    }

    public void setride_date(String ride_date) {
        this.ride_date = ride_date;
    }


    public void setSample(JSONArray sample) {
        this.sample = sample;
    }

    public JSONArray getSample() {
        return this.sample;
    }
}
