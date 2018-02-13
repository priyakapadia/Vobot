package com.wowwee.chip_android_sampleproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;

/**
 * Created by davidchan on 22/3/2017.
 */

public class MenuFragment extends ChipBaseFragment {
    TextView voiceToText;

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

        ListView listView = (ListView) view.findViewById(R.id.menuTable);
        String[] robotNameArr = {"Interation", "Setting", "Learning Session"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, robotNameArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
               if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
                    ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new PlayingFragment(), R.id.view_id_content, false);
                            break;

                        case 1:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ModuleSettingFragment(), R.id.view_id_content, false);
                            break;
                        case 2:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new SpeechAce(), R.id.view_id_content, false);
                            break;
                    }
               }
            }
        });

       ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
        robot.setCallbackInterface(this);

        return view;


    }


}
