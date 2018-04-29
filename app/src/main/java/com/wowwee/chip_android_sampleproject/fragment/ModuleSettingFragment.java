package com.wowwee.chip_android_sampleproject.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.MainActivity;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

/**
 * ModuleSettingFragment controls the settings page where the user can control CHiP's settings and check its battery life.
 */

public class ModuleSettingFragment extends ChipBaseFragment {
    Handler handler;
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

        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        final ChipRobot robot = ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
        robot.setCallbackInterface(this);

        handler = new Handler();

        final ListView listView = (ListView)view.findViewById(R.id.menuTable);
        String[] ledNameArr = {"Back", "Detect Battery", "Get Volume", "Set Volume", "Get Eye Brightness", "Set Eye Brightness", "Force Sleep", "Send CHiP to Charge"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, ledNameArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
                            break;
                        case 1:
                            robot.chipGetBatteryLevel();
                            break;
                        case 2:
                            robot.chipGetVolume();
                            break;
                        case 3:
                            setVolume();
                            break;
                        case 4:
                            robot.chipGetEyeRGBBrightness();
                            break;
                        case 5:
                            setEyeRGBBrightness();
                            break;
                        case 6:
                            Toast.makeText(getActivity(), "Turn off and on CHIP to stop sleeping", Toast.LENGTH_LONG)
                                    .show();
                            robot.chipForceSleep();
                            break;
                        case 7:
                            robot.chipPlayBodycon((byte)(16));
                            break;
                    }
                }
            }
        });

        return view;
    }

    void setVolume() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Volume (0-11)");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChipRobot robot = ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                int value = Integer.valueOf(input.getText().toString());
                if (value < 0 || value > 11) {
                    Toast.makeText(getActivity(), "Invalid Volume!", Toast.LENGTH_LONG)
                            .show();
                } else {
                    robot.chipSetVolumeLevel(value);
                }
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.cancel();

            }
        });
        builder.show();
    }

    void setEyeRGBBrightness() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eye RGB Brightness (0-255)");
        final EditText input1 = new EditText(getActivity());
        input1.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input1);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ChipRobot robot = ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                int value = Integer.valueOf(input1.getText().toString());
                if (value < 0 || value > 255){
                    Toast.makeText(getActivity(), "Invalid Eye Brightness!", Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    robot.chipSetEyeRGBBrightness((byte) value);
                }
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(input1.getWindowToken(), 0);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(input1.getWindowToken(), 0);
                dialog.cancel();

            }
        });
        builder.show();
    }

    void setAdultOrKidSpeed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Adult or Kid Speed (0-Adult, 1-Kid)");
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ChipRobot robot = ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                int value = Integer.valueOf(input.getText().toString());
                if (value == 0)
                    robot.chipSetAdultOrKidSpeed(ChipCommandValues.kChipSpeed.kChipAdultSpeed);
                else
                    robot.chipSetAdultOrKidSpeed(ChipCommandValues.kChipSpeed.kChipKidSpeed);
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                        dialog.cancel();

                    }
                });
        builder.show();
    }

    @Override
    public void chipDidReceiveDogVersionWithBodyHardware(final int chipVerBodyHardwareVer, final int chipVerHeadHardware, final int chipVerMechanic, final int chipVerBleSpiFlash, final int chipVerNuvotonSpiFlash, final int chipVerBleBootloader, final int chipVerBleApromFirmware, final int chipVerNuvotonBootloaderFirmware, final int chipVerNuvotonApromFirmware, final int chipVerNuvotonVR) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Firmware");
                builder.setMessage((int)chipVerNuvotonBootloaderFirmware + "");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void chipDidReceiveVolumeLevel(ChipRobot chip, final byte volume) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Volume");
                builder.setMessage((int)volume + "");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void chipDidReceivedBatteryLevel(ChipRobot chip, final float batteryLevel, byte chargingStatus, byte chargerType) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Battery Level");
                builder.setMessage((batteryLevel*100) + "%");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void chipDidReceiveAdultOrKidSpeed(final byte speed) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Adult Or Kid Speed");
                if (speed == 0)
                    builder.setMessage("Adult Speed");
                else
                    builder.setMessage("Kid Speed");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public void chipDidReceiveEyeRGBBrightness(final byte value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Eye RGB Brightness");
                builder.setMessage((int)value + "");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

}
