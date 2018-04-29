package com.wowwee.chip_android_sampleproject.utils;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.wowwee.chip_android_sampleproject.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
The ScoreHelper class was created to modularize all functionality of the speech scoring API LearningSession. It serves to provide 2 main functions:
1. Get a similarity score from a particular word
2. Get the prompt to be played for a word

The class sends http requests to LearningSession. The API key is embedded in the URL, which is a private variable for the class.
*/

public class ScoreHelper {
    private static String SPEECH_ACE_URL = "http://api.speechace.co/api/scoring/text/v0.1/json?key=qkfwDzHl27%2BXibxYwaCF2Gt1KVktUtqrxGIRMfzcrDSaESeSkpcXidvleR4G5vvn%2BLoVLwmWbo%2BVlu4twd%2FfgLNiBp0RCEoQA0wYhf9pme3cU0Yopx6Bvm4yHpsrcNWY%0A%0A%20&dialect=en-us&user_id=001";

    //Sends SpeechAce the recorded audio file, and the word to compare. Returns a score
    public static String sendRequestToSpeechAce(File file, String word){
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg3"), file);
        //RequestBody requestFile = RequestBody.create(MediaType.parse("audio/x-wav"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("user_audio_file", "apple", requestFile);
        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(fileBody)
                .addFormDataPart("text", word)
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

    //Requests an audio file from SpeechAce for prompting the child of a word
    public static long playPrompt(FragmentActivity activity, String word){

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        String getPromptString =  "http://api.speechace.co/api/ttsing/text/v0.1/wav?key=qkfwDzHl27%2BXibxYwaCF2Gt1KVktUtqrxGIRMfzcrDSaESeSkpcXidvleR4G5vvn%2BLoVLwmWbo%2BVlu4twd%2FfgLNiBp0RCEoQA0wYhf9pme3cU0Yopx6Bvm4yHpsrcNWY&dialect=en-us&user_id=3332&text=" + word;
        long duration_of_prompt = 0;
        long duration_of_beep;
        Request request = new Request.Builder()
                .url(getPromptString)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/audioprompt.mp4");
            fos.write(response.body().bytes());
            fos.close();
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/audioprompt.mp4");
            mp.prepare();
            duration_of_prompt = mp.getDuration();
            Log.d("duration of prompt", String.valueOf(duration_of_prompt));
            mp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        MediaPlayer mpPrompt = new MediaPlayer();
        try {
            mpPrompt.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/audioprompt.mp4");
            mpPrompt.prepare();
            mpPrompt.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //waiting for prompt to end
        try {
            if (duration_of_prompt < 1000 ){
                Thread.sleep(1000);

            }
            else {
                Thread.sleep(duration_of_prompt);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Play beep sound after prompt
        MediaPlayer beep= MediaPlayer.create(activity, R.raw.beep_09);
        duration_of_beep = beep.getDuration();
        beep.start();
        return duration_of_beep;
    }
}
