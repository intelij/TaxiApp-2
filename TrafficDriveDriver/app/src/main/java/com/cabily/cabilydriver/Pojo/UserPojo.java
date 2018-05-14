package com.cabily.cabilydriver.Pojo;

import java.util.ArrayList;

/**
 * Created by user88 on 7/10/2017.
 */

public class UserPojo {




    private String max_no_of_seat="";

    public String getNo_of_seat() {
        return this.no_of_seat;
    }

    public void setNo_of_seat(String no_of_seat) {
        this.no_of_seat = no_of_seat;
    }

    public String getMax_no_of_seat() {
        return this.max_no_of_seat;
    }

    public void setMax_no_of_seat(String max_no_of_seat) {
        this.max_no_of_seat = max_no_of_seat;
    }

    private String no_of_seat="";
    private String need_payment="";
    private String receive_cash="";
    private String req_payment="";
    private String total_payable_amount="";
    private String share_id="";
    private String ride_id="";
    private String ride_status="";
    private String btn_group="";
    private String user_id="";
    private String user_name="";
    private String ride_type="";
    private String user_email="";
    private String phone_number="";
    private String user_image="";
    private String user_review="";
    private String pickup_location="";
    private String pickup_lat="";
    private String pickup_lon="";
    private String pickup_time="";
    private String drop_location="";
    private String drop_lat="";
    private String drop_lon="";
    private String selected_pos="";
    private String active_ride_id;

    public ArrayList<FarePojo> getFarelist() {
        return this.farelist;
    }

    public void setFarelist(ArrayList<FarePojo> farelist) {
        this.farelist = (ArrayList<FarePojo>)farelist.clone();
    }

    private ArrayList<FarePojo> farelist;

    public String getRide_type() {
        return this.ride_type;
    }

    public void setRide_type(String ride_type) {
        this.ride_type = ride_type;
    }

    public String getActive_ride_id() {

        return this.active_ride_id;
    }

    public String getShare_id() {
        return this.share_id;
    }

    public void setShare_id(String share_id) {
        this.share_id = share_id;
    }



    public void setActive_ride_id(String active_ride_id) {
        this.active_ride_id = active_ride_id;
    }

    public String getSelected_pos() {
        return this.selected_pos;

    }

    public void setSelected_pos(String selected_pos) {
        this.selected_pos = selected_pos;
    }

    public String getRide_id() {
        return this.ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getRide_status() {
        return this.ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }

    public String getBtn_group() {
        return this.btn_group;
    }

    public void setBtn_group(String btn_group) {
        this.btn_group = btn_group;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return this.user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getPhone_number() {
        return this.phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_image() {
        return this.user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_review() {
        return this.user_review;
    }

    public void setUser_review(String user_review) {
        this.user_review = user_review;
    }

    public String getPickup_location() {
        return this.pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getPickup_lat() {
        return this.pickup_lat;
    }

    public void setPickup_lat(String pickup_lat) {
        this.pickup_lat = pickup_lat;
    }

    public String getPickup_lon() {
        return this.pickup_lon;
    }

    public void setPickup_lon(String pickup_lon) {
        this.pickup_lon = pickup_lon;
    }

    public String getPickup_time() {
        return this.pickup_time;
    }

    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }

    public String getDrop_location() {
        return this.drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDrop_lat() {
        return this.drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_lon() {
        return this.drop_lon;
    }

    public void setDrop_lon(String drop_lon) {
        this.drop_lon = drop_lon;
    }

    public String getNeed_payment() {
        return this.need_payment;
    }

    public void setNeed_payment(String need_payment) {
        this.need_payment = need_payment;
    }

    public String getReceive_cash() {
        return this.receive_cash;
    }

    public void setReceive_cash(String receive_cash) {
        this.receive_cash = receive_cash;
    }

    public String getReq_payment() {
        return this.req_payment;
    }

    public void setReq_payment(String req_payment) {
        this.req_payment = req_payment;
    }

    public String getTotal_payable_amount() {
        return this.total_payable_amount;
    }

    public void setTotal_payable_amount(String total_payable_amount) {
        this.total_payable_amount = total_payable_amount;
    }

    }
