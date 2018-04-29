package com.wowwee.chip_android_sampleproject.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipCommandValues;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

/**
 SoundFragment controls all of WowWee CHiP's sounds
 */

public class SoundFragment extends ChipBaseFragment {

    Handler handler;
    ListView listView;

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

        if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
            ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
            robot.setCallbackInterface(SoundFragment.this);
        }
        listView = (ListView)view.findViewById(R.id.menuTable);
        String[] listArr = {"Back", "Stop", "Bark Angry", "Bark Curious Playful Happy", "Bark Neutral", "Bark Scared", "Bark Angry 2", "Bark Curious Playful Happy 2", "Bark Neutral 2", "Bark Scared 2", "Cry", "Growl 1", "Growl 2", "Growl 3", "HUH Long", "HUH Short", "Lick 1", "Lick 2", "Pant Fast", "Pant Medium", "Pant Slow", "Sniff 1", "Sniff 2", "Yawn 1", "Yawn 2", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Zero", "Cough", "Cry 2", "Cry 3", "Cry 4", "Cry 5", "Emo Curious 1", "Emo Curious 2", "Emo Curious 3", "Emo Excited 1", "Emo Excited 2", "Emo Excited 3", "Emo Lazy 1", "Emo Lazy 2", "Emo Lazy 3", "Emo Response 1", "Emo Response 2", "Emo Response 3", "Emo Scared Yip 1", "Emo Scared Yip 2", "Fart 1", "Fart 2", "Fart 3", "Growl 1", "Growl 2", "Growl 3", "Growl 4", "Hiccup 1", "Hiccup 2", "Howl 1", "Howl 2", "Howl 3", "Howl 4", "Howl 5", "Lick 2", "Lick 3", "Low Battery 1", "Low Battery 2", "Muffle 1", "Muffle 2", "Muffle 3", "Pant 1", "Pant 2", "Pant 3", "Pant 4", "Pant 5", "Smooch 1", "Smooch 2", "Smooch 3", "Sneeze 1", "Sneeze 2", "Sneeze 3", "Sniff 1", "Sniff 2", "Sniff 3", "Snore 1", "Snore 2", "Special 1", "Sing D0 Short 1", "Sing D0 Short 2", "Sing FA Short", "Sing LA Short", "Sing MI Short", "Sing RE Short", "Sing SO Short", "Sing TI Short", "Bark 3", "Bark 4", "Bark 5", "Bark Multi 3 ", "Bark Multi 4", "Bark Multi 5", "Burp 1", "Burp 2", "Cough 1", "Emo Response 3A", "Demo Music 2", "Demo Music 3", "Bark 1", "Bark 2", "Bark Multi 1", "Bark Multi 2", "Cry 1", "Emo Curious", "Emo Excited", "Emo Lazy 1", "Emo Lazy 2", "Emo Lazy 3", "Growl 3", "Howl 1", "Howl 2", "Howl 3", "Howl 4", "Lick 1", "Low Battery 1A", "Low Battery 2A", "Muffle", "Smooch", "Sneeze", "Sniff 3", "Sniff 4", "Music Demo", "Emo Response 1A", "Emo Response 2A", "Short Mute for Stop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, listArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
                    ChipRobot robot = (ChipRobot)ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new PlayingFragment(), R.id.view_id_content, false);
                            break;
                        case 1:
                            robot.chipStopSound();
                            break;
                        default:
                            ChipCommandValues.kChipSoundFileValue value = ChipCommandValues.kChipSoundFileValue.kChipSoundFile_None;
                            value.setValue(position-1);
                            robot.chipPlaySound(value);
                            break;
                    }
                }
            }
        });

        handler = new Handler();

        return view;
    }

}
