package com.example.user.parkinglot;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

public class Start extends Activity{
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.start);
        frameLayout = (FrameLayout)findViewById(R.id.id_content) ;
        frameLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(R.id.id_content, new LoginFragment(),"ONE");
        tx.commit();
    }
}