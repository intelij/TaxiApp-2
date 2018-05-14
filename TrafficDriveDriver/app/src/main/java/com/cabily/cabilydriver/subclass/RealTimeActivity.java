package com.cabily.cabilydriver.subclass;

import android.os.Bundle;

import com.app.xmpp.ChatConfigurationBuilder;
import com.cabily.cabilydriver.Utils.SessionManager;
import com.cabily.cabilydriver.adapter.ContinuousRequestAdapter;

/**
 * Created by Administrator on 3/17/2016.
 */
public class RealTimeActivity extends SubclassActivity {

    protected ChatConfigurationBuilder builder;
    protected String chatID;
    private static SessionManager session;
    String hostURL = "", hostName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        session = new SessionManager(this);
        if (session != null && session.getUserDetails() != null) {
            hostURL = session.getXmpp().get(SessionManager.KEY_HOST_URL);
            hostName = session.getXmpp().get(SessionManager.KEY_HOST_NAME);
        }
        chatID = ContinuousRequestAdapter.userID + "@" + hostName;
        builder = new ChatConfigurationBuilder(this);
        builder.createConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (builder != null)
            builder.closeConnection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null)
            builder.closeConnection();
    }
}
