package com.wowwee.chip_android_sampleproject.fragment;

import android.content.Context;
import android.media.AudioManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SpeechAce extends Fragment {

    private static MediaRecorder recorder;
    AudioManager audioManager;
    private Spinner spinner1;
    private TableRow table0, table1, table2, table3;
    private TextView syllable1_value, syllable2_value, syllable3_value, score1_value, score2_value, score3_value;

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

        final View view = inflater.inflate(R.layout.fragment_speech_ace, container, false);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        addListenerOnSpinnerItemSelection(view); //call spinner function

        Button sendRequest = (Button)view.findViewById(R.id.sendRequest);
        table0 = (TableRow) view.findViewById(R.id.tableRow0);
        //table0.setVisibility(v.VISIBLE);

        table1 = (TableRow) view.findViewById(R.id.tableRow1);


        table2 = (TableRow) view.findViewById(R.id.tableRow2);
        table3 = (TableRow) view.findViewById(R.id.tableRow3);


        syllable1_value = (TextView) view.findViewById(R.id.syllable1);
        score1_value = (TextView) view.findViewById(R.id.score1);

        syllable2_value = (TextView) view.findViewById(R.id.syllable2);
        score2_value = (TextView) view.findViewById(R.id.score2);

        syllable3_value = (TextView) view.findViewById(R.id.syllable3);
        score3_value = (TextView) view.findViewById(R.id.score3);



        //setContentView(R.layout.fragment_speech_ace);
        final TextView httpResponseText = (TextView) view.findViewById(R.id.response);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResponseFromSpeechAce(v, httpResponseText);
               // playPrompt(prompt);
            }
        });

        Button backButton = (Button)view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
            }
        });




        return view;
    }

    public void rewardDog(){
        if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
            ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
//            ChipCommandValues.kChipSoundFileValue value = ChipCommandValues.kChipSoundFileValue.kChipSoundFile_None;
//            value.setValue(110);
//            robot.chipPlaySound(value);

            robot.chipPlayBodycon((byte)(5));
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.chipPlayBodycon((byte) (1));
        }
    }


    public void playPrompt(){

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        String getPromptString = "http://api.speechace.co/api/ttsing/text/v0.1/wav?key=qkfwDzHl27%2BXibxYwaCF2Gt1KVktUtqrxGIRMfzcrDSaESeSkpcXidvleR4G5vvn%2BLoVLwmWbo%2BVlu4twd%2FfgLNiBp0RCEoQA0wYhf9pme3cU0Yopx6Bvm4yHpsrcNWY&dialect=en-us&user_id=3332&text=" + String.valueOf(spinner1.getSelectedItem());

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

    }

    public void getResponseFromSpeechAce(View v, TextView httpResponseText) {
        playPrompt();
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

        // String path = "/storage/emulated/0/Download/api-samples/api-samples/";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";
        File file = new File(path, "audiorecorded.mp4");


        // Send Request with the audio file to SpeechAce
        String responseText = sendRequestToSpeechAce(file);

        //Get quality score - returns -1 if errored out
        if (!responseText.equals("Error")) {
            // Get quality score for each syllable. Returns null if there was an error
            final LinkedHashMap<String, Double> syllableToQualityScore = getQualityScore(responseText);

            change_graph(syllableToQualityScore, v);

            Collection<Double> cumulativeQScoreList = syllableToQualityScore.values();

            Double cumulativeQualityScore = 0.0;
            for(Double singleQualityScore: cumulativeQScoreList){
                cumulativeQualityScore += singleQualityScore;
            }
            cumulativeQualityScore = cumulativeQualityScore/cumulativeQScoreList.size();
            int scoreToPrint = (cumulativeQualityScore).intValue();
            String outputText = "Total Score: " +  scoreToPrint + "/100";
            httpResponseText.setText(outputText);

            if (cumulativeQualityScore > 30){
                //reward-dog
                rewardDog();
            }
            else {
                outputText = "Total Score: "+ scoreToPrint + "/100. Please prompt the child again.";
                httpResponseText.setText(outputText);
            }
        }
    }

    private String sendRequestToSpeechAce(File file){
        //RequestBody requestFile = RequestBody.create(MediaType.parse("audio/mpeg3"), file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/x-wav"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("user_audio_file", "apple", requestFile);
        MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(fileBody)
                .addFormDataPart("text", (String.valueOf(spinner1.getSelectedItem()))) //"apple"
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


    // Get quality score for each syllable. Returns null if there was an error
    private LinkedHashMap<String, Double> getQualityScore(final String responseText) {

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
            JSONArray jsonWordScore = (JSONArray) jsonTextScore.get("word_score_list");
            return parseAllWords(jsonWordScore);
        }
        return null;
    }

    private LinkedHashMap<String, Double> parseAllWords(final JSONArray wordList){
        LinkedHashMap<String, Double> syllableToQualityScore = new LinkedHashMap<>();
        for (JSONObject wordJsonObject : (Iterable<JSONObject>) wordList) {
            JSONArray syllableList = (JSONArray) wordJsonObject.get("syllable_score_list");

            for (JSONObject syllableObject : (Iterable<JSONObject>) syllableList) {
                String letters = (String) syllableObject.get("letters");
                Double qualityScore = (Double) syllableObject.get("quality_score");
                syllableToQualityScore.put(letters, qualityScore);
            }
        }

        return syllableToQualityScore;
    }


    private void beginRecording() throws IOException {

        //set audio source to bluetooth
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.startBluetoothSco();
        audioManager.setBluetoothScoOn(true);

        //start recording
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

         //stop audio from mic
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);

    }


    public void addListenerOnSpinnerItemSelection(View v) {
        spinner1 = (Spinner) v.findViewById(R.id.spinner_word_choice);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_word_choice, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                Log.v("item", (String) parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }



    private void change_graph( LinkedHashMap<String, Double> map_of_scores, View v) {

        int counter = 0;
        table1.setVisibility(v.INVISIBLE);
        table2.setVisibility(v.INVISIBLE);
        table3.setVisibility(v.INVISIBLE);

        for (String key : map_of_scores.keySet()) {

            if (counter == 0) {
                table1.setVisibility(v.VISIBLE);
                syllable1_value.setText(key);
                score1_value.setText(String.valueOf(map_of_scores.get(key)));
            }

            else if (counter ==1){
               table2.setVisibility(v.VISIBLE);
                syllable2_value.setText(key);
                score2_value.setText(String.valueOf(map_of_scores.get(key)));
            }

            else {
                table3.setVisibility(v.VISIBLE);
                syllable3_value.setText(key);
                score3_value.setText(String.valueOf(map_of_scores.get(key)));
            }

            counter ++;

        }
    }

}












