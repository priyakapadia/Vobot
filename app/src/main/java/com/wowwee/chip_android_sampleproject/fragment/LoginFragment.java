package com.wowwee.chip_android_sampleproject.fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wowwee.chip_android_sampleproject.R;
import com.wowwee.chip_android_sampleproject.utils.FragmentHelper;
import com.wowwee.chip_android_sampleproject.utils.SQLHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 LoginFragment controls the log in page.
 Every account is associated with a phone number, and is the only parameter required to log in.
 The page is only displayed once, after which the phone number is stored in the phone's memory.
 If the user deletes and reinstalls the app, they would only need to put in the phone number to retrieve all the data.
 */

public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText phoneNumber = (EditText)view.findViewById(R.id.phone_number);

        //Set Preference
        final SharedPreferences myPrefs = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor;
        prefsEditor = myPrefs.edit();
//        Button backButton = (Button) view.findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new MenuFragment(), R.id.view_id_content, false);
//            }
//        });

        Button post_button = (Button)view.findViewById(R.id.post_number);
        post_button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);

                if (SQLHelper.createNewUser(phoneNumber.getText().toString())) {
                    prefsEditor.putString("LoginPhone", phoneNumber.getText().toString()); //save number to preferences
                    prefsEditor.apply();
                    Toast.makeText(getActivity(),"Logged in!",Toast.LENGTH_SHORT).show();
                    FragmentHelper.switchFragment(getActivity().getSupportFragmentManager(), new ConnectFragment(), R.id.view_id_content, false);
                }
                else {
                    Log.d("DB ERROR", "Couldn't create a new user!");
                    Toast.makeText(getActivity(),"Failed to create a new user!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}

