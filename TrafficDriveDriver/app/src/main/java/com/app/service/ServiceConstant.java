package com.app.service;/** * Created by Administrator on 10/1/2015. */public interface ServiceConstant {   /* String MAIN_URL = "http://192.168.1.251:8081/product-working/dectar/cabily/";    String URL_SHARE = MAIN_URL + "v7/api/";    String URL = MAIN_URL + "v7/";    String BASE_URL = URL + "api/v1/";    String baseurl = URL;*/    String MAIN_URL = "http://trafficdrive.bg/";    String URL_SHARE = MAIN_URL + "v7/api/";    String URL = MAIN_URL + "v7/";    String BASE_URL = URL + "api/v1/";    String baseurl = URL;    /*String MAIN_URL = "http://project.dectar.com/cabilydemo/";    String URL_SHARE = MAIN_URL + "v7/api/";    String URL = MAIN_URL + "v7/";    String BASE_URL = URL + "api/v1/";    String baseurl = URL;*/    String LOGIN_URL = URL_SHARE + "driver/login";    String Register_URL = MAIN_URL + "app/driver/signup";    String privacy_policy_URL = MAIN_URL + "pages/privacy-and-policy";    String Register_Return_URL = "app/driver/signup/success";    String UPDATE_CURRENT_LOCATION = URL_SHARE + "location/update/driver";    String UPDATE_AVAILABILITY = URL_SHARE + "driver/update/availability";    String ACCEPTING_RIDE_REQUEST = URL_SHARE + "booking/accept";    String CANCELLATION_REQUEST = BASE_URL + "provider/cancellation-reason";    String CANCEL_RIDE_REQUEST = BASE_URL + "provider/cancel-ride";    String ARRIVED_REQUEST = BASE_URL + "provider/arrived";    String BEGIN_RIDE_REQUEST = BASE_URL + "provider/begin-ride";    String END_RIDE_REQUEST = BASE_URL + "provider/end-ride";    String LOGOUT_REQUEST = URL_SHARE + "driver/logout";    String TRIP_LIST_REQUEST = BASE_URL + "provider/my-trips/list";    String TRIP_VIEW_REQUEST = BASE_URL + "provider/my-trips/view";    public static String loginurl = BASE_URL + "provider/login";    String DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyCWIwBjGj-jM-nm8yyPZpHt7ZWmkm6vC44&origins=ORIGIN_STRING&destinations=DESTINATION_STRING";    String place_search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?&key=AIzaSyCWIwBjGj-jM-nm8yyPZpHt7ZWmkm6vC44&input=";    String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyCWIwBjGj-jM-nm8yyPZpHt7ZWmkm6vC44&placeid=";    public static String forgotpassword = URL_SHARE + "driver/password/forgot";    public static String changepassword = URL_SHARE + "driver/password/change";    public static String driver_dashboard = URL_SHARE + "driver/dashboard";    public static String XmppServerUpdate = URL_SHARE + "update-ride-location";    String phoneMasking_sms = URL_SHARE + "masking/sms";    public static String phoneMasking = URL_SHARE + "masking/call";    public static String getBankDetails = URL_SHARE + "driver/banking/get";    public static String saveBankDetails = URL_SHARE + "driver/banking/save";    public static String paymentdetails_url = URL_SHARE + "driver/payment/list";    public static String paymentdetails_lis_url = URL_SHARE + "driver/payment/summary";    public static String trip_Track_Driver = URL_SHARE + "booking/track/driver";    public static String tripsummery_view_url = URL_SHARE + "driver/trip/view";    public static String tripsummery_list_url = URL_SHARE + "driver/trip/list";    public static String ridecancel_reason_url = URL_SHARE + "booking/cancel/reason";    public static String ridecancel_url = URL_SHARE + "booking/cancel";    public static String arrivedtrip_url = URL_SHARE + "trip/arrived";    public static String send_report = URL_SHARE + "report/send";    public static String acknowledge_ride = URL_SHARE + "trip/request/ack";    public static String delete_ride = URL_SHARE + "trip/request/deny";    public static String begintrip_url = URL_SHARE + "trip/begin";    public static String endtrip_url = URL_SHARE + "trip/end";    public static String receivecash_url = BASE_URL + "provider/receive-payment";    public static String receivedbill_amounr_cash_url = URL_SHARE + "trip/payment/received";    public static String reviwes_options_list_url = URL_SHARE + "reviews/options";    public static String submit_reviwes_url = URL_SHARE + "reviews/submit";    public static String request_paymnet_url = URL_SHARE + "trip/payment/request";    public static String skip_reviwes_url = URL_SHARE + "reviews/skip";    public static String check_trip_status = URL_SHARE + "trip/check";    public static String change_Language = URL_SHARE + "language/update";    public static String app_launching_url = URL_SHARE + "get-app-info";    String updateAppStatus_url = URL_SHARE + "notification/status";    // http://192.168.1.251:8081/suresh/dectarfortaxi/api/v3/check-trip-status    //----------------Push notification Key--------------------------    String ACTION_LABEL = "action";    String ACTION_TAG_RIDE_REQUEST = "ride_request";    String ACTION_TAG_RIDE_CANCELLED = "ride_cancelled";    String ACTION_TAG_RECEIVE_CASH = "receive_cash";    String ACTION_TAG_PAYMENT_PAID = "payment_paid";    String ACTION_TAG_NEW_TRIP = "new_trip";    String ACTION_RIDE_COMPLETED = "ride_completed";    String ACTION_ACTION_HOCKYAPPID = "9f8e1861d5cc413ba593e3367676bca3";    public static String useragent = "cabily2k15android";    public static String isapplication = "1";    public static String applanguage = "en";    public static String cabily_AppType = "android";    String pushNotification_Ads = "ads";    String Ads_title = "key1";    String Ads_Message = "key2";    String Ads_image = "key3";}