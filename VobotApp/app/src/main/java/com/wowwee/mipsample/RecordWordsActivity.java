package com.wowwee.mipsample;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wowwee.bluetoothrobotcontrollib.MipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.MipRobotSound;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * Created by priyak on 11/11/17.
 */
public class RecordWordsActivity extends Activity {

    private TextView voiceToText;
    private static final int REQ_CODE_VOICE_IN = 143;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_words);
        TextView tv = (TextView) findViewById(R.id.date);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = sdf.format(date);
        tv.setText(dateString);


        voiceToText = (TextView) findViewById(R.id.voiceIn);
        Button speakPromt = (Button) findViewById(R.id.btnToSpeak);
        speakPromt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceToTextService();
            }
        });
    }

    private void startVoiceToTextService() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //You can set here own local Language.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi, Talk to Me");
        try {
            startActivityForResult(intent, REQ_CODE_VOICE_IN);
        }
        catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_VOICE_IN: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceToText.setText(result.get(0));
                    playSound();
                }
                break;
            }

        }
    }

    public void playSound(){
        List<MipRobot> mips = MipRobotFinder.getInstance().getMipsConnected();
        Random r = new Random();
        for (MipRobot mip : mips) {
            // more sounds can be added
            mip.mipPlaySound(MipRobotSound.create(MipCommandValues.kMipSoundFile_MIP_IN_LOVE));
            break;
        }
    }
}
