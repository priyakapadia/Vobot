package com.wowwee.chip_android_sampleproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobot;
import com.wowwee.bluetoothrobotcontrollib.chip.ChipRobotFinder;
import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;
import com.wowwee.chip_android_sampleproject.utils.ListAdapter;

/**
 * MenuFragment displays the main menu consisting of the Learning Session, Graphs, Interaction, and Settings page.
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
        String[] robotNameArr = {"Learning Session", "Graphs","Interaction", "Settings", };

        Integer ImageName[] = {
                R.drawable.learning_session_image,
                R.drawable.graphs_background,
                R.drawable.interaction_background,
                R.drawable.settings_background
        };

        ListAdapter listAdapter = new ListAdapter(this.getActivity(), robotNameArr, ImageName);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() > 0) {
                    ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
                    switch (position) {
                        case 0:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new LearningSession(), R.id.view_id_content, false);
                            break;
                        case 1:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new GraphFragment(), R.id.view_id_content, false);
                            break;
                        case 2:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new PlayingFragment(), R.id.view_id_content, false);
                            break;
                        case 3:
                            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ModuleSettingFragment(), R.id.view_id_content, false);
                            break;
                    }
                }
                else {
                    Toast.makeText(getActivity(),"CHiP Disconnected!",Toast.LENGTH_SHORT).show();
                    FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
                    }
                }
                //}
            //}
        });

        if (ChipRobotFinder.getInstance().getChipRobotConnectedList().size() == 0) {
            Toast.makeText(getActivity(),"CHiP Disconnected!",Toast.LENGTH_SHORT).show();
            FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
        }
        else {
            ChipRobot robot = (ChipRobot) ChipRobotFinder.getInstance().getChipRobotConnectedList().get(0);
            robot.setCallbackInterface(this);
        }


        return view;


    }


}
