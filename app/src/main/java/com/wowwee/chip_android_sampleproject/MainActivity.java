package com.wowwee.chip_android_sampleproject;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.wowwee.bluetoothrobotcontrollib.BluetoothRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.fragment.ConnectFragment;
import com.wowwee.chip_android_sampleproject.fragment.LoginFragment;
import com.wowwee.chip_android_sampleproject.fragment.MenuFragment;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    public static final String PREFS_NAME = "MyPrefsfile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter.getDefaultAdapter();


        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);


        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean(  "isFirstRun", true); //true equals first run


        int spinner_position = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getInt(  "spinnerPosition", 5); //true equals first run


        String LoginPhone = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString(  "LoginPhone", ""); //login phone number


        if (isFirstRun) {

            Toast.makeText(MainActivity.this, "First Run", Toast.LENGTH_LONG)
                    .show();
            FragmentHelper.switchFragment(getSupportFragmentManager(), new LoginFragment(), R.id.view_id_content, false);
        }

        else{
            FragmentHelper.switchFragment(getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
            Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ChipRobot robot : (List<ChipRobot>)ChipRobotFinder.getInstance().getChipRobotConnectedList()){
            robot.disconnect();
        }
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // disable idle timer
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (ChipRobot robot : (List<ChipRobot>)ChipRobotFinder.getInstance().getChipRobotConnectedList()){
            robot.disconnect();
        }

        BluetoothRobot.unbindBluetoothLeService(MainActivity.this);

        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
