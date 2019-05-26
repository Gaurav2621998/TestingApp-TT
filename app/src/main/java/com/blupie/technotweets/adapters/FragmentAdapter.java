package com.blupie.technotweets.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.blupie.technotweets.fragments.Monday;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private final Monday m1=new Monday();
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        return mFragmentList.get(i);
    }



    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

    }


    public void addFragment(Fragment fragment, String title) {

        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);

    }

    public String title(int position)
    {
        return mFragmentTitleList.get(position);
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);

        switch (position)
        {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";

                default: return null;

        }

    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentAdapter.POSITION_NONE;

    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
