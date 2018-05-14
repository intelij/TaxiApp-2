package com.app.xmpp;

import android.content.Context;
import android.util.Log;

import com.cabily.cabilydriver.Utils.SessionManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;


/**
 */
public class ChatConfigurationBuilder implements ChatManagerListener, ChatMessageListener {

    private static final String TAG = "Chat Constant";
    private AbstractXMPPConnection connection;
    private int TOTAL_TIME_OUT = 30000;
    private SessionManager session;
    private ChatManager chatManager;
    private Chat chat = null;
    private int packetTimeout = 10000;
    public static boolean isConnected_new;
    private Context context1;
    String hostURL = "", hostName = "";
    public ChatConfigurationBuilder(Context context) {

        context1 = context;
        session = new SessionManager(context);
    }

    public void createConnection() {
        if (session != null && session.getUserDetails() != null) {
            hostURL = session.getXmpp().get(SessionManager.KEY_HOST_URL);
            hostName = session.getXmpp().get(SessionManager.KEY_HOST_NAME);
        }

        System.out.println("Xmpp URL and NAME"+hostName+" "+hostURL);
                XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
                configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                createSSLContext();
        configBuilder.setHost(hostURL);//http://192.168.1.116/67.219.149.186
        configBuilder.setServiceName(hostName);
                createConnection(configBuilder);



    }

    private void createConnection(XMPPTCPConnectionConfiguration.Builder configBuilder) {
        connection = new XMPPTCPConnection(configBuilder.build());
        connection.setPacketReplyTimeout(TOTAL_TIME_OUT);
      if(!isConnected_new) {
          connect();
          login_conn();
      }
    }
    public void login_conn()
    {
        String userName = "", password = "";
        if (session != null && session.getUserDetails() != null) {
            userName = session.getUserDetails().get(SessionManager.KEY_DRIVERID);
            password = session.getUserDetails().get(SessionManager.KEY_SEC_KEY);
        }
        try {
            connection.login(userName, password);
            connection.setPacketReplyTimeout(packetTimeout);
            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addChatListener(this);
            connection.addConnectionListener(mConnectionListener);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public void connect()
{
    try {
        connection.connect();
    } catch (SmackException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (XMPPException e) {
        e.printStackTrace();
    }
}
    public Chat createChat(String chatID) {
        synchronized (ChatConfigurationBuilder.class) {
            if (chatID != null && chatManager != null) {
                chat = chatManager.createChat(chatID);
            }
            return chat;
        }
    }

    public String sendMessage(String chatID, String message) {
        if (chat == null) {
            chat = createChat(chatID);
            return "0";
        } else {
            try {

                //  Toast.makeText(context1,chatID,Toast.LENGTH_SHORT).show();
                chat.sendMessage(message);
                return "1";
            } catch (SmackException.NotConnectedException e) {
                try {
                    chat = chatManager.createChat(chatID);
                    chat.sendMessage(message);
                    return "1";
                } catch (SmackException.NotConnectedException e1) {
                    Log.d(TAG, "Not able to Send Live Stream");
                    XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
                    configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                    createSSLContext();
                    if (session != null && session.getUserDetails() != null) {
                        hostURL = session.getXmpp().get(SessionManager.KEY_HOST_URL);
                        hostName = session.getXmpp().get(SessionManager.KEY_HOST_NAME);
                    }
                    configBuilder.setHost(hostURL);//http://192.168.1.116/67.219.149.186
                    configBuilder.setServiceName(hostName);
                    connection = new XMPPTCPConnection(configBuilder.build());
                    connection.setPacketReplyTimeout(TOTAL_TIME_OUT);
                    try {
                        connection.connect();
                    } catch (SmackException se) {
                        se.printStackTrace();
                    } catch (IOException io) {
                        io.printStackTrace();
                    } catch (XMPPException xmppe) {
                        xmppe.printStackTrace();
                    }
                    String userName = "", password = "";
                    if (session != null && session.getUserDetails() != null) {
                        userName = session.getUserDetails().get(SessionManager.KEY_DRIVERID);
                        password = session.getUserDetails().get(SessionManager.KEY_SEC_KEY);
                    }
                    try {
                        connection.login(userName, password);
                        connection.setPacketReplyTimeout(packetTimeout);
                        chatManager = ChatManager.getInstanceFor(connection);
                        chatManager.addChatListener(this);
                        connection.addConnectionListener(mConnectionListener);
                    } catch (XMPPException xmmppe) {
                        xmmppe.printStackTrace();
                    } catch (SmackException smacke) {
                        smacke.printStackTrace();
                    } catch (IOException IOE) {
                        IOE.printStackTrace();
                    }
                    return "0";
                }
            } catch (Exception e) {
                connect();
              //  login_conn();
                e.printStackTrace();
                return "0";
            }
        }
    }


    public void closeConnection() {
        if (connection != null) {
            connection.disconnect();
        }
        connection = null;
        chatManager = null;
    }


    private void createSSLContext() {
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
        }
    }


    ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            isConnected_new = true;

            //Code to send request to disable chat
              /*  ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(ChatingService.this, "available");
                chatAvailability.postChatRequest();*/
        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

        }

        @Override
        public void connectionClosed() {
            isConnected_new = false;
               /* ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(ChatingService.this, "available");
                chatAvailability.postChatRequest();
*/
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            isConnected_new = false;
               /* ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(ChatingService.this, "available");
                chatAvailability.postChatRequest();*/

        }

        @Override
        public void reconnectionSuccessful() {
            isConnected_new = true;
               /* ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(ChatingService.this, "available");
                chatAvailability.postChatRequest();*/

        }

        @Override
        public void reconnectingIn(int seconds) {
        }

        @Override
        public void reconnectionFailed(Exception e) {
            isConnected_new = false;

               /* ChatAvailabilityCheck chatAvailability = new ChatAvailabilityCheck(ChatingService.this, "available");
                chatAvailability.postChatRequest();*/

        }

    };

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
    }

    @Override
    public void processMessage(Chat chat, Message message) {
    }
}
