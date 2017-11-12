package com.wowwee.mipsample;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.wowwee.bluetoothrobotcontrollib.MipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.MipRobotSound;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobot.MipRobotInterface;
import com.wowwee.bluetoothrobotcontrollib.sdk.MipRobotFinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainMenuActivity extends FragmentActivity implements MipRobotInterface{

	private BluetoothAdapter mBluetoothAdapter;
    private Spinner spinner1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main_menu);
		
		final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		
		// Set BluetoothAdapter to MipRobotFinder
		MipRobotFinder.getInstance().setBluetoothAdapter(mBluetoothAdapter);
		
		// Set Context to MipRobotFinder
		MipRobotFinder finder = MipRobotFinder.getInstance();
		finder.setApplicationContext(getApplicationContext());
		toggleConnectButton();
		addListenerOnSpinnerItemSelection();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.registerReceiver(mMipFinderBroadcastReceiver, MipRobotFinder.getMipRobotFinderIntentFilter());
		if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
           if (!mBluetoothAdapter.isEnabled()) {
               TextView noBtText = (TextView)this.findViewById(R.id.no_bt_text);
               noBtText.setVisibility(View.VISIBLE);
           }
		}
		
		// Search for mip
		MipRobotFinder.getInstance().clearFoundMipList();
		scanLeDevice(false);
//		updateMipList();
		scanLeDevice(true);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.unregisterReceiver(mMipFinderBroadcastReceiver);
		for(MipRobot mip : MipRobotFinder.getInstance().getMipsConnected()) {
			mip.readMipHardwareVersion();
			mip.disconnect();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		System.exit(0);
	}

    private void toggleConnectButton(){
        TextView connectionStatusText = (TextView) findViewById(R.id.connect_text);
        Button connectButton = (Button) findViewById(R.id.connect_to_bluetooth);
        if("MiP not found.".equals(connectionStatusText.getText().toString())) {
            connectButton.setVisibility(View.VISIBLE);
        } else {
            connectButton.setVisibility(View.GONE);
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.response_arrays, android.R.layout.simple_spinner_item);
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



    public void buttonOnclickHandler(View view){

        switch (view.getId()){
            case R.id.connect_to_bluetooth:
            {
                connectToBluetooth();
            }
            break;
            case R.id.perform_action:
            {
                performSelectedResponse(spinner1.getSelectedItemPosition());

            }
            break;
            case R.id.get_started:
            {
                // Do we want Activity or Fragment?
                Intent intentRecordWords = new Intent(MainMenuActivity.this, RecordWordsActivity.class);
                MainMenuActivity.this.startActivity(intentRecordWords);
            }
            break;
            case R.id.drive:
                DriveViewFragment fragment = new DriveViewFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.view_id_drive_layout, fragment);
                transaction.attach(fragment);
                transaction.commit();
                break;
        }

		
	}
    private void connectToBluetooth(){
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);

//        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
    }

    private void performSelectedResponse(int selectedFromDropDown){
        // Related to the dropdown menu
        //If Play sound is selected
        switch (selectedFromDropDown){
            case 0:
                playSound();
                break;
            case 1:
                dance();
                break;
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

    public void dance(){
        // TODO change dancing as this code is just moving MiP right
        List<MipRobot> mips = MipRobotFinder.getInstance().getMipsConnected();
        for (MipRobot mip : mips) {
            mip.mipPunchRightWithSpeed(10);
        }

    }

	private void scanLeDevice(final boolean enable) {
        if (enable) {
        	// Scan for MiP/Coder MiP/Turbo Dave
        	MipRobotFinder.getInstance().scanForAllRobots();
        	// Scan for MiP/Coder MiP
//        	MipRobotFinder.getInstance().scanForMips();
        	// Scan for Turbo Dave
//        	MipRobotFinder.getInstance().scanForMinions();
        } else {
            MipRobotFinder.getInstance().stopScanForMips();
        }
    }
	
	public void updateMipList()
	{
		//connect to first found mip
		List<MipRobot> mipFoundList = MipRobotFinder.getInstance().getMipsFoundList();
		for(MipRobot mipRobot : mipFoundList) {
			connectToMip(mipRobot);
			break;
		}
	}

	private void connectToMip(final MipRobot mip) {
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mip.setCallbackInterface(MainMenuActivity.this);
				mip.connect(MainMenuActivity.this.getApplicationContext());
				TextView connectionView = (TextView)MainMenuActivity.this.findViewById(R.id.connect_text);
				connectionView.setText("Connecting: "+mip.getName());
			}
		});
		
	}
	
//	@Override
	public void mipDeviceReady(MipRobot sender) {
		final MipRobot robot = sender;
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView connectionView = (TextView)MainMenuActivity.this.findViewById(R.id.connect_text);
				connectionView.setText("Connected: "+robot.getName());
			}
		});
	}

	@Override
	public void mipRobotDidReceiveHardwareVersion(MipRobot mip,int mipHardwareVersion,
			int mipVoiceFirmwareVersion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveSoftwareVersion(Date mipFirmwareVersionDate,
			int mipFirmwareVersionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveVolumeLevel(MipRobot mip,int mipVolume) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveIRCommand(MipRobot mip,ArrayList<Byte> irDataArray,
			int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveWeightReading(MipRobot mip,byte value,
			boolean leaningForward) {
		final String weightLevel = "level " + value + "!";
		// TODO Auto-generated method stub

	}

	@Override
	public void mipDeviceDisconnected(MipRobot sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveBatteryLevelReading(MipRobot mip, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceivePosition(MipRobot mip, byte position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotIsCurrentlyInBootloader(MipRobot mip,
			boolean isBootloader) {
		// TODO Auto-generated method stub
		
	}

	private final BroadcastReceiver mMipFinderBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MipRobotFinder.MipRobotFinder_MipFound.equals(action)) {
            	// Connect to mip
            	final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						 List<MipRobot> mipFoundList = MipRobotFinder.getInstance().getMipsFoundList();
						 if (mipFoundList != null && mipFoundList.size() > 0){
							 MipRobot selectedMipRobot = mipFoundList.get(0);
							  if (selectedMipRobot != null){
								  connectToMip(selectedMipRobot);
							  }
						 }
					}
				}, 1);
				 
            }
        }
	};


	@Override
	public void mipRobotDidReceiveClapDetectionStatusIsEnabled(MipRobot arg0, boolean arg1, long arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveGesture(MipRobot arg0, byte arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mipRobotDidReceiveNumberOfClaps(MipRobot arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
