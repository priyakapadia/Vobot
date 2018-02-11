package com.wowwee.chip_android_sampleproject.fragment;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
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

import com.wowwee.bluetoothrobotcontrollib.chip.ChipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpeechAce extends Fragment {

    private static MediaRecorder recorder;

    private static String SPEECH_ACE_URL = "http://api.speechace.co/api/scoring/text/v0.1/json?key=qkfwDzHl27%2BXibxYwaCF2Gt1KVktUtqrxGIRMfzcrDSaESeSkpcXidvleR4G5vvn%2BLoVLwmWbo%2BVlu4twd%2FfgLNiBp0RCEoQA0wYhf9pme3cU0Yopx6Bvm4yHpsrcNWY%0A%0A%20&dialect=en-us&user_id=001";
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
                getResponseFromSpeechAce(v, httpResponseText);
            }
        });

        return view;
    }

    public void rewardDog(){
        if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
            ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
            ChipCommandValues.kChipSoundFileValue value = ChipCommandValues.kChipSoundFileValue.kChipSoundFile_None;
            value.setValue(110);
            robot.chipPlaySound(value);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.chipStopSound();
        }
    }

    public void getResponseFromSpeechAce(View v, TextView httpResponseText) {

        final MediaPlayer mp_mother = MediaPlayer.create(getActivity(), R.raw.prompt_apple);
        mp_mother.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //REWARD
        rewardDog();

        Log.d("RECORD", "GOING TO START RECORDING");

        //record
        try {
            beginRecording();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("RECORD", "IN PROGRESS");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //stop + save
        stopRecording();
        Log.d("RECORD", "RECORDING FINISHED");

        //String path = "/storage/emulated/0/Download/api-samples/api-samples/";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";
        Log.d("PATH", path);
        File file = new File(path, "audiorecorded.mp4");

        // Send Request with the audio file to SpeechAce
        String responseText = sendRequestToSpeechAce(file);

        //Get quality score - returns -1 if errored out
        if (!responseText.equals("Error")) {
            double qualityScore = getQualityScore(responseText);
            System.out.println("Quality Score: " + qualityScore + "/100");
            String outputText = "Total Score: "+  qualityScore + "/100";
            httpResponseText.setText(outputText);
            if (qualityScore > 50){
                //reward-dog
                rewardDog();
            }
            else {
                outputText = "Total Score: "+ qualityScore + "/100. Please prompt the child again.";
                httpResponseText.setText(outputText);
            }
        }
    }

    private String sendRequestToSpeechAce(File file){
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg3"), file);
        //RequestBody requestFile = RequestBody.create(MediaType.parse("audio/x-wav"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("user_audio_file", "apple", requestFile);
        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(fileBody)
                .addFormDataPart("text", "apple")
                .addFormDataPart("user_id", "1234")
                .build();

        Request request = new Request.Builder()
                                     .url(SPEECH_ACE_URL)
                                     .post(body)
                                     .build();
        try {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            return response.body().string();
        }
        catch (IOException e) {

            return "Error";
        }
    }

    private double getQualityScore(String responseText) {

        JSONObject jsonObject = null;

        try {
            Object jsonToObject = new JSONParser().parse(responseText);
            jsonObject = (JSONObject) jsonToObject;
        }
        catch (ParseException e) {
            System.err.println("Error while parsing Json, with message: " + e);
        }

        if(null != jsonObject) {
            JSONObject jsonTextScore = (JSONObject) jsonObject.get("text_score");
            return (double) jsonTextScore.get("quality_score");
        }

        return -1;
    }

    private void beginRecording() throws IOException {
        ditchMediaRecorder();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/audiorecorded.mp4";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(path);
        recorder.prepare();
        recorder.start();

        //Toast.makeText(getApplicationContext(), "starting", Toast.LENGTH_LONG).show();

    }

    private void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();
    }

    private void stopRecording() {

        if(recorder!=null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            //Toast.makeText(getApplicationContext(), "stopping",Toast.LENGTH_LONG).show();
        }


    }

}

