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
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotConstants;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

/**
 AnimationFragment controls all of WowWee CHiP's animations
 */

public class AnimationFragment extends ChipBaseFragment {

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
            robot.setCallbackInterface(AnimationFragment.this);
        }
        listView = (ListView)view.findViewById(R.id.menuTable);
        String[] listArr = {"Back", "Reset", "Sit", "Lie Down", "Idle", "Dance", "VR Training 1", "VR Training 2", "Reset 2", "Jump", "Yoga", "Come to watch holder", "Follow to watch holder", "Fetch ball", "Track the ball", "Play soccer with the ball", "Return to Base", "Dance to Base", "Stop or stand from Base", "Guard Mode", "Free Roam", "Face down for controlling Chippies"};
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
                        default:
                            robot.chipPlayBodycon((byte)(position));
                            break;
                    }
                }
            }
        });

        handler = new Handler();

        return view;
    }

}
