package com.wowwee.chip_android_sampleproject.utils;


import android.os.StrictMode;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static okhttp3.Protocol.get;

public class SQLHelper {

    //Public client to send all requests
    public static OkHttpClient client = new OkHttpClient();
    private static String databaseURL = "http://34.239.153.228:8080";

    //Post all session information to SQL
    public static boolean postSession(String score, String level, String phoneNumber, String sessionNumber, String overall_score, String childs_name ,String childWord ){

        RequestBody body = new FormBody.Builder()
                .add("phone_number", phoneNumber)
                .add("session", sessionNumber)
                .add("overall_score", overall_score)
                .add("childs_word", childWord)
                .add("childs_name", childs_name)
                .add("indiv_score", score)
                .add("level", level)
                .build();

        Request request = new Request.Builder()
                .url(databaseURL + "/sessions")
                .post(body)
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                return (isSuccessful(response.body().string()));
            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        return false;
    }

    public static boolean postProgress(String level, String phoneNumber, String sessionNumber, String overall_score, String counter, String childs_name ,String childWord ){

        RequestBody body = new FormBody.Builder()
                .add("phone_number", phoneNumber)
                .add("overall_sessions", sessionNumber)
                .add("overall_score", overall_score)
                .add("childs_word", childWord)
                .add("childs_name", childs_name)
                .add("counter", counter)
                .add("level", level)
                .build();

        Request request = new Request.Builder()
                .url(databaseURL + "/progress")
                .post(body)
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                return (isSuccessful(response.body().string()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public static String CheckScore( String phoneNumber,String childWord, String score,  String childs_name ){

        RequestBody body = new FormBody.Builder()
                .add("phone_number", phoneNumber)
                .add("childs_word", childWord)
                .add("childs_name", childs_name)
                .add("indiv_score", score)
                .build();

        Request request = new Request.Builder()
                .url(databaseURL + "/calc_val")
                .post(body)
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                return ((response.body().string()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    //Create new user
    public static boolean createNewUser(String phoneNumber){

        RequestBody body = new FormBody.Builder()
                .add("phone_number", phoneNumber)
                .build();

        Request request = new Request.Builder()
                .url(databaseURL + "/accounts")
                .post(body)
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                return (isSuccessful(response.body().string()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;

    }

    //Get all sessions and scores for a particular child and word
    public static double [] getSessionScores(String phoneNumber, String word) {

        double [] session = null;

        Request request = new Request.Builder()
                .url(databaseURL + "/sessions/graph/" + phoneNumber + "&" + word)
                .get()
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                String responseText = response.body().string();
                session = make_graph_array(responseText);
                return session;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return session;
    }

    public static String CheckLevel( String phoneNumber,String childWord, String childs_name ){

        Request request = new Request.Builder()
                .url(databaseURL + "/calc_level/"+ phoneNumber + "&" + childWord + "&" + childs_name)
                .get()
                .build();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {
                Response response = client.newCall(request).execute();
                String responseText = response.body().string();
                String level = GetLevel(responseText);
                return level;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "";
    }

    public static double [] make_graph_array(String responseText) {
        JSONObject jsonObject = null;
        double[] y= new double[100];

        try {
            Object jsonToObject = new JSONParser().parse(responseText);
            jsonObject = (JSONObject) jsonToObject;

        } catch (ParseException e) {
            System.err.println("Error while parsing JSON, with message: " + e);
        }

        if (null != jsonObject) {
            boolean jsonTextScore = (boolean) jsonObject.get("error");

            if(!jsonTextScore){
                JSONArray jsonarray;


                jsonarray = (JSONArray) jsonObject.get("data");


                int counter = 0;
                for (JSONObject wordJsonObject : (Iterable<JSONObject>) jsonarray) {

                    long indiv_score = (long) wordJsonObject.get("indiv_score");

                    y[counter]= (double) indiv_score;
                    counter ++;


                }

                double [] final_array = new double [counter];

                for (int i =0; i < counter; i++){
                    final_array[i] = y[i];

                }
                return final_array;
            }
        }

        return y;
    }

    public static String GetLevel(String responseText) {
        JSONObject jsonObject = null;


        try {
            Object jsonToObject = new JSONParser().parse(responseText);
            jsonObject = (JSONObject) jsonToObject;

        } catch (ParseException e) {
            System.err.println("Error while parsing JSON, with message: " + e);
        }

        if (null != jsonObject) {
            boolean jsonTextScore = (boolean) jsonObject.get("error");

            if(!jsonTextScore){
                JSONArray jsonarray;


                jsonarray = (JSONArray) jsonObject.get("data");

                if (jsonarray == null){
                    return "1";
                }
                else {
                    for (JSONObject wordJsonObject : (Iterable<JSONObject>) jsonarray) {

                        long level = (long) wordJsonObject.get("level");

                        return Long.toString(level);


                    }
                }

            }
        }

        return "";
    }

    //Check if call to database was successful
    public static boolean isSuccessful(String responseText) {
        JSONObject jsonObject = null;

        try {
            Object jsonToObject = new JSONParser().parse(responseText);
            jsonObject = (JSONObject) jsonToObject;

        } catch (ParseException e) {
            System.err.println("Error while parsing JSON, with message: " + e);
        }

        if (null != jsonObject) {
            boolean jsonTextScore = (boolean) jsonObject.get("error");
            return (!jsonTextScore);
        }
        return false;
    }
}
