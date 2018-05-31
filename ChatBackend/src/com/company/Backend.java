package com.company;

import com.google.gson.Gson;
import utils.Message;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Backend {

    private final String SEND_MESSAGE_URL = "sendMessage";
    private final String GET_MESSAGES_URL = "getMessages";
    private final String GET_CLIENTS_URL = "getOnlineClients";
    private final String LOGIN_URL = "login";
    private final String LOGOUT_URL = "logout";

    private String url = null;

    private String ip = null;
    private int port = 0;

    private Gson jsonConvert;

    public Backend() {
        Properties config = new Properties();
        InputStream stream = null;

        try{
            stream = new FileInputStream("./config.properties");
            config.load(stream);
            ip = config.getProperty("server");
            port = Integer.parseInt(config.getProperty("port"));
            stream.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }

        url = "http://" + ip + ":" + port + "/";


        jsonConvert = new Gson();
    }

    /**
     * @param userName Name of the User that wants to login
     * */
    public void login(String userName) {
        String payload = "cName:" + userName;
        try {
            postRequest(url + LOGIN_URL, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userName Name of the user that wants to logout
     * */
    public void logout(String userName) {
        String payload = "cName:" + userName;
        try {
            postRequest(url + LOGOUT_URL, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param msg Message that needs to be send to the server
     * */
    public void sendMessage(Message msg) {
        String payload = jsonConvert.toJson(msg);
        try {
            postRequest(url + SEND_MESSAGE_URL, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param userName Name of the Client
     * @param chatPartner Name of the chat partner
     * @return Returns a List of Message Objects
     * */
    public List<Message> getMessages(String userName, String chatPartner) {
        String params = "cName=" + userName + "&chatPartner=" + chatPartner;

        MessageResponse response = null;

        try {
            response = jsonConvert.fromJson(getRequest(url + GET_MESSAGES_URL, params), MessageResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.messages;
    }

    /**
     * @return Returns a List of Strings, which contain the names of the clients
     * */
    public List<String> getClients() {
        ClientList clientList = null;

        try {
            clientList = jsonConvert.fromJson(getRequest(url + GET_CLIENTS_URL, null), ClientList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientList.clients;
    }

    private String getRequest(String requestUrl, String params) throws Exception {

        String urlStr = "";

        if(params != null) {
            urlStr = requestUrl + "/?" + params;
        }else {
            urlStr = requestUrl;
        }

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line = "";
        StringBuffer response = new StringBuffer();

        while( (line = reader.readLine()) != null ) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    /**
     * @param requestUrl URL to the server
     * @param payload payload for the POST request
     *                Method does a POST request to the server
     *                Returns immediatly when there is no payload set
     * */
    private void postRequest(String requestUrl, String payload) throws Exception {

        if(payload == null || payload.equals("")) {
            return;
        }

        URL url = new URL(requestUrl);

        byte[] postDataBytes = payload.getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);

        int responseCode = conn.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer buffer = new StringBuffer();
        String line = "";

        while((line = in.readLine()) != null) {
            buffer.append(line);
        }
        in.close();
    }
}

class ClientList {
    public List<String> clients = new ArrayList<String>();
}

class MessageResponse {
    public List<Message> messages = new ArrayList<Message>();
}
