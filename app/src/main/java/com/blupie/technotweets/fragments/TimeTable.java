package com.blupie.technotweets.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.blupie.technotweets.R;
import com.blupie.technotweets.adapters.FragmentAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

public class TimeTable extends Fragment {



    SmartTabLayout tabLayout;

    ViewPager viewPager;

    String arr[];

    public static String deptsem="CSE 1st Year";

    Spinner spinner;



     public TimeTable() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arr=getContext().getResources().getStringArray(R.array.Branchyear);

        tabLayout=(SmartTabLayout) view.findViewById(R.id.tab1);
        viewPager=(ViewPager)view.findViewById(R.id.view);
        spinner=(Spinner)view.findViewById(R.id.deptsem);

        viewPager.setOffscreenPageLimit(6);

        final FragmentAdapter[] fragmentAdapter = {new FragmentAdapter(getChildFragmentManager())};
        fragmentAdapter[0].addFragment(new Monday(),"Monday");
        fragmentAdapter[0].addFragment(new Tuesday(),"Tuesday");
        fragmentAdapter[0].addFragment(new Wednesday(),"Wednesday");
        fragmentAdapter[0].addFragment(new Thursday(),"Thursday");
        fragmentAdapter[0].addFragment(new Friday(),"Friday");
        fragmentAdapter[0].addFragment(new Saturday(),"Saturday");



        viewPager.setAdapter(fragmentAdapter[0]);

        tabLayout.setViewPager(viewPager);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                
                // set the selected value in deptsem and reload all fragment
                deptsem=arr[position];

            //    viewPager.setCurrentItem(0);
             //   viewPager.removeAllViews();
            //    viewPager.setAdapter(fragmentAdapter[0]);

                //fragmentAdapter[0].getItemPosition(8);

                Toast.makeText(getContext(), ""+deptsem, Toast.LENGTH_SHORT).show();

           //     fragmentAdapter[0].getItemPosition(fragmentAdapter[0].getItem(position));

//                fragmentAdapter[0].clear();
//
//                fragmentAdapter[0] =new FragmentAdapter(getChildFragmentManager());
//                fragmentAdapter[0].addFragment(new Monday(),"Monday");
//                fragmentAdapter[0].addFragment(new Tuesday(),"Tuesday");
//                fragmentAdapter[0].addFragment(new Wednesday(),"Wednesday");
//                fragmentAdapter[0].addFragment(new Thursday(),"Thursday");
//                fragmentAdapter[0].addFragment(new Friday(),"Friday");
//                fragmentAdapter[0].addFragment(new Saturday(),"Saturday");
//
//                viewPager.setAdapter(fragmentAdapter[0]);
//                tabLayout.setViewPager(viewPager);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

     }



}
