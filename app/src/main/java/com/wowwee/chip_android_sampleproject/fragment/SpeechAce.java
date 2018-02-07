package com.wowwee.chip_android_sampleproject.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wowwee.chip_android_sampleproject.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpeechAce extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null)
            return null;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);

        View view = inflater.inflate(R.layout.fragment_speech_ace, container, false);

        Button sendRequest = (Button)view.findViewById(R.id.sendRequest);
        //setContentView(R.layout.fragment_speech_ace);
        final TextView httpResponseText = (TextView) view.findViewById(R.id.response);
        sendRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendToSpeechAce(v, httpResponseText);
            }
        });

        return view;
    }

    public void sendToSpeechAce2(View v, TextView httpResponseText){

    }

    public void sendToSpeechAce(View v, TextView httpResponseText){

        final MediaPlayer mp_mother = MediaPlayer.create(getActivity(), R.raw.mother);
        mp_mother.start();
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String responseText = "Error";

        OkHttpClient client = new OkHttpClient();
        //String path = "/storage/emulated/0/Download/api-samples/api-samples/";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/Download/api-samples/";
        Log.d("PATH", path);
        File file = new File(path, "apple.wav");
        //File file = new File(path, "shinsuke_nakamura.mp3");

        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/x-wav"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("user_audio_file", "apple", requestFile);
        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(fileBody)
                .addFormDataPart("text", "apple")
                .addFormDataPart("user_id", "1234")
                .build();

        Request request = new Request.Builder()
                .url("https://api.speechace.co/api/scoring/text/v0.1/json?key=qkfwDzHl27%2BXibxYwaCF2Gt1KVktUtqrxGIRMfzcrDSaESeSkpcXidvleR4G5vvn%2BLoVLwmWbo%2BVlu4twd%2FfgLNiBp0RCEoQA0wYhf9pme3cU0Yopx6Bvm4yHpsrcNWY%0A%0A%20&dialect=en-us&user_id=001")
                .post(body)
                .build();

        Response response;

        try {
            response = client.newCall(request).execute();
           responseText = response.body().string();


        } catch (IOException e) {
            responseText= "Error";
        }

        //httpResponseText.setMovementMethod(new ScrollingMovementMethod());
        httpResponseText.setText(responseText);

        //Get quality score
//        if (!responseText.equals("Error")){
//            JSONObject jsonObject = new JSONObject(responseText);
//            JSONArray params = jsonObject.getJSONArray("text_score");
//
//            String ticket = params.getString("quality_score");
//            Log.d("Score", ticket);
//        }
    }
}
