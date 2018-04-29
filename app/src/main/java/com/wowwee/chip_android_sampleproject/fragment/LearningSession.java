package com.wowwee.chip_android_sampleproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
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
import android.widget.Toast;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.MainActivity;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;
import com.wowwee.chip_android_sampleproject.utils.SQLHelper;
import com.wowwee.chip_android_sampleproject.utils.ScoreHelper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 LearningSession controls the learning page.

 Sequence of steps:
 1. The user chooses a specific word on the spinner
 2. When the button for "Start Session" is clicked, the phone plays a sample audio file of the chosen word
 3. The child mimics the word
 4. The child's attempt is compared to the model word, and a similarity score is computed using SpeechAce
 5. CHiP robot dog provides positive reinforcement upon a level increment
 6. A table is displayed for the user with a breakdown of syllables and corresponding scores
 7. The database is updated for both the session and the word
 */

public class LearningSession extends Fragment {

    private static MediaRecorder recorder;
    long duration_of_beep;
    AudioManager audioManager;
    int spinnerPosition;
    String phonenum;
    private Button backButton, sendRequestButton;
    private Spinner spinner1;
    private TableRow table0, table1, table2, table3, table4, table5, table6, table7;
    private TextView syllableheading, scoreheading, tv1,syllable1_value, syllable2_value, syllable3_value, score1_value, score2_value, score3_value, syllable4_value, syllable5_value, syllable6_value, score4_value, score5_value, score6_value, syllable7_value, score7_value;
    private long mLastClickTime = 0;

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

        //call spinner function
        addListenerOnSpinnerItemSelection(view);

        spinnerPosition=  this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getInt(  "spinnerPosition", 0);

        //Display the level of the word chosen on the spinner by querying the database
        tv1 = (TextView) view.findViewById(R.id.level);
        phonenum = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString(  "LoginPhone", ""); //login phone number

        spinner1.setSelection(spinnerPosition);
        String level = SQLHelper.CheckLevel(phonenum,String.valueOf(spinner1.getSelectedItem()), "phillis");
        if (level.equals("")){
            Toast.makeText(getActivity(),"Database is not connected. Could not retrieve current level of word. Please check your internet connection.",Toast.LENGTH_SHORT).show();
            tv1.setText("Current Level: Error [Database not connected]");
        }
        else {
            tv1.setText("Current Level: " + level);
        }

        //Assign variables to all table rows and values to change them following a session
        table0 = (TableRow) view.findViewById(R.id.tableRow0);
        table1 = (TableRow) view.findViewById(R.id.tableRow1);
        table2 = (TableRow) view.findViewById(R.id.tableRow2);
        table3 = (TableRow) view.findViewById(R.id.tableRow3);
        table4 = (TableRow) view.findViewById(R.id.tableRow4);
        table5 = (TableRow) view.findViewById(R.id.tableRow5);
        table6 = (TableRow) view.findViewById(R.id.tableRow6);
        table7 = (TableRow) view.findViewById(R.id.tableRow7);

        syllableheading = (TextView) view.findViewById(R.id.syllableheading);
        scoreheading = (TextView) view.findViewById(R.id.scoreheading);
        syllable1_value = (TextView) view.findViewById(R.id.syllable1);
        score1_value = (TextView) view.findViewById(R.id.score1);
        syllable2_value = (TextView) view.findViewById(R.id.syllable2);
        score2_value = (TextView) view.findViewById(R.id.score2);
        syllable3_value = (TextView) view.findViewById(R.id.syllable3);
        score3_value = (TextView) view.findViewById(R.id.score3);
        syllable4_value = (TextView) view.findViewById(R.id.syllable4);
        score4_value = (TextView) view.findViewById(R.id.score4);
        syllable5_value = (TextView) view.findViewById(R.id.syllable5);
        score5_value = (TextView) view.findViewById(R.id.score5);
        syllable6_value = (TextView) view.findViewById(R.id.syllable6);
        score6_value = (TextView) view.findViewById(R.id.score6);
        syllable7_value = (TextView) view.findViewById(R.id.syllable7);
        score7_value = (TextView) view.findViewById(R.id.score7);

        //Set headings for table invisible
        table0.setVisibility(INVISIBLE);
        syllableheading.setVisibility(INVISIBLE);
        scoreheading.setVisibility(INVISIBLE);


       //Session button event handler to start a learning session
        final TextView httpResponseText = (TextView) view.findViewById(R.id.response);

        sendRequestButton = (Button)view.findViewById(R.id.sendRequest);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 4000){
                    if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() == 0) {
                        Toast.makeText(getActivity(), "CHiP Disconnected!", Toast.LENGTH_SHORT).show();
                        FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
                    } else {
                        startSession(v, httpResponseText);
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                }
                else{
                    mLastClickTime = SystemClock.elapsedRealtime();
                }

            }
        });

        //Back button event handler to return to menu
         backButton = (Button)view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() == 0) {
                    Toast.makeText(getActivity(),"CHiP Disconnected!",Toast.LENGTH_SHORT).show();
                    FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
                }
                else {
                    FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
                }
            }
        });

        return view;
    }


    //rewardDog is called to provide positive reinforcement to the child. It makes CHiP dance for 7 seconds, then resets the robot.
    public void rewardDog(){
        if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
            ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
            //Play animation 5 = dance
            //Play sound 110 =  demo music 2
            //Play sound 111 = demo music 3

            Random rand = new Random();
            int randomValue = rand.nextInt(3);
            if (randomValue == 0){
                robot.chipPlayBodycon((byte)(5));
            }
            else if (randomValue == 1){
                ChipCommandValues.kChipSoundFileValue value = ChipCommandValues.kChipSoundFileValue.kChipSoundFile_None;
                value.setValue(110);
                robot.chipPlaySound(value);
            }
            else {
                ChipCommandValues.kChipSoundFileValue value = ChipCommandValues.kChipSoundFileValue.kChipSoundFile_None;
                value.setValue(111);
                robot.chipPlaySound(value);
            }
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Reset dog
            robot.chipPlayBodycon((byte)(1));
            robot.chipStopSound();
        }
    }

    //Function starts up an entire session
    public void startSession(View v, TextView httpResponseText) {

        //Play prompt
        duration_of_beep = ScoreHelper.playPrompt(getActivity(), String.valueOf(spinner1.getSelectedItem()));

        //record audio
        try {
           Thread.sleep(duration_of_beep-300);
            beginRecording();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("RECORDING", "Started Recording");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //stop + save
        stopRecording();
        Log.d("RECORDING", "Completed Recording");

        // String path = "/storage/emulated/0/Download/api-samples/api-samples/";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/";
        File file = new File(path, "audiorecorded.mp4");

        // Send Request with the audio file to LearningSession
        String responseText = ScoreHelper.sendRequestToSpeechAce(file, (String.valueOf(spinner1.getSelectedItem())));

        //Get quality score - returns -1 if error
        if (!responseText.equals("Error")) {

            // Get quality score for each syllable. Returns null if there was an error
            final LinkedHashMap<String, Double> syllableToQualityScore = getQualityScore(responseText);

            //Display table of scores
            change_graph(syllableToQualityScore, v);

            Collection<Double> cumulativeQScoreList = syllableToQualityScore.values();

            Double cumulativeQualityScore = 0.0;
            for(Double singleQualityScore: cumulativeQScoreList){
                cumulativeQualityScore += singleQualityScore;
            }
            cumulativeQualityScore = cumulativeQualityScore/cumulativeQScoreList.size();
            int scoreToPrint = (cumulativeQualityScore).intValue();
            String outputText = "Total Score: " +  scoreToPrint + "/100";
            httpResponseText.setText(outputText); //Updating page

            //Get phone number from the phones storage to access the DB
            String phonenum = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .getString(  "LoginPhone", ""); //login phone number

            //Query DB to check if the level has been incremented
            String responseScore = SQLHelper.CheckScore(phonenum, String.valueOf(spinner1.getSelectedItem()), cumulativeQualityScore.toString(), "phillis");
            String level, session_number, overall_score, counter;

            if (!responseScore.equals("")){
                JSONObject jsonObject = null;
                try {
                    Object jsonToObject = new JSONParser().parse(responseScore);
                    jsonObject = (JSONObject) jsonToObject;

                } catch (ParseException e) {
                    System.err.println("Error while parsing JSON, with message: " + e);
                }
                if (null != jsonObject) {
                    boolean jsonTextScore = (boolean) jsonObject.get("error");
                    if (!jsonTextScore){
                        boolean levelbool = (boolean) jsonObject.get("levelbool");
                        if (levelbool){
                            //Reward the child if the level has incremented
                           rewardDog();
                            Log.d("Reward","Level incremented! Congratulations");
                        }
                        else {
                            Log.d("Reward", "Level did not increment.");
                        }
                        counter = String.valueOf(jsonObject.get("counter"));
                        level = String.valueOf(jsonObject.get("level"));
                        session_number = String.valueOf(jsonObject.get("session"));
                        overall_score = String.valueOf(jsonObject.get("overall_score"));

                        //Updating database
                        if (SQLHelper.postSession(cumulativeQualityScore.toString(), level,phonenum, session_number, overall_score, "phillis",String.valueOf(spinner1.getSelectedItem()))){
                            Log.d("DB Update", "Added word to database");
                            String CurrentLevel = SQLHelper.CheckLevel(phonenum, String.valueOf(spinner1.getSelectedItem()), "phillis");
                            if (CurrentLevel.equals("")){
                                Toast.makeText(getActivity(),"Database is not connected. Could not retrieve current level of word. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                                tv1.setText("Current Level: Error [Database not connected]");
                            }
                            else {
                                tv1.setText("Current Level: " + level);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(),"Database is not connected. Could not update sessions. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                            Log.d("DB Update", "Could not update session");
                        }

                        if (SQLHelper.postProgress( level,phonenum, session_number, overall_score, counter,"phillis",String.valueOf(spinner1.getSelectedItem()))){
                            Log.d("DB Update", "Updated progress table");
                           // String CurrentLevel = SQLHelper.CheckLevel(phonenum, String.valueOf(spinner1.getSelectedItem()), "phillis");
                            //tv1.setText("Current Level: " + CurrentLevel);

                        }
                        else {
                            Toast.makeText(getActivity(),"Database is not connected. Could not update progress. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                            Log.d("DB Update", "Could not update progress table ");
                        }

                        //public static boolean postProgress(String level, String phoneNumber, String sessionNumber, String overall_score, String childs_name ,String childWord )
                    }
                }
            }
            else {
                Toast.makeText(getActivity(),"Database is not connected. Could not compare levels. Please check your internet connection.",Toast.LENGTH_SHORT).show();
            }

        }

       // backButton.setVisibility(v.VISIBLE);
       // sendRequestButton.setVisibility(v.VISIBLE);
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
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(path);
        recorder.prepare();
        recorder.start();
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

        //        TextView tv1 = (TextView)view.findViewById(R.id.level);
        //        String phonenum = view.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        //                        .getString(  "LoginPhone", ""); //login phone number
                final SharedPreferences myPrefs = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                final SharedPreferences.Editor prefsEditor;
                prefsEditor = myPrefs.edit();
                prefsEditor.putInt("spinnerPosition", Integer.valueOf(String.valueOf(spinner1.getSelectedItemPosition()))); //save number to preferences
                prefsEditor.apply();
                String level = SQLHelper.CheckLevel(phonenum,String.valueOf(spinner1.getSelectedItem()), "phillis");
                if (level.equals("")){
                    Toast.makeText(getActivity(),"Database is not connected. Could not retrieve current level of word. Please check your internet connection.",Toast.LENGTH_SHORT).show();
                    tv1.setText("Current Level: Error [Database not connected]");
                }
                else {
                    tv1.setText("Current Level: " + level);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    //Change the table with the updated scores
    private void change_graph( LinkedHashMap<String, Double> map_of_scores, View v) {

        int counter = 0;
        table1.setVisibility(INVISIBLE);
        table2.setVisibility(INVISIBLE);
        table3.setVisibility(INVISIBLE);
        table4.setVisibility(INVISIBLE);
        table5.setVisibility(INVISIBLE);
        table6.setVisibility(INVISIBLE);
        table7.setVisibility(INVISIBLE);

        //Set titles to visible
        table0.setVisibility(VISIBLE);
        syllableheading.setVisibility(VISIBLE);
        scoreheading.setVisibility(VISIBLE);

        for (String key : map_of_scores.keySet()) {

            if (counter == 0) {
                table1.setVisibility(VISIBLE);
                syllable1_value.setText(key);
                score1_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else if (counter == 1){
               table2.setVisibility(VISIBLE);
                syllable2_value.setText(key);
                score2_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else if (counter == 2){
                table3.setVisibility(VISIBLE);
                syllable3_value.setText(key);
                score3_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else if (counter == 3){
                table4.setVisibility(VISIBLE);
                syllable4_value.setText(key);
                score4_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else if (counter == 4){
                table5.setVisibility(VISIBLE);
                syllable5_value.setText(key);
                score5_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else if (counter == 5){
                table6.setVisibility(VISIBLE);
                syllable6_value.setText(key);
                score6_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            else {
                table7.setVisibility(VISIBLE);
                syllable7_value.setText(key);
                score7_value.setText(String.valueOf(map_of_scores.get(key)));
            }
            counter ++;
        }
    }
}