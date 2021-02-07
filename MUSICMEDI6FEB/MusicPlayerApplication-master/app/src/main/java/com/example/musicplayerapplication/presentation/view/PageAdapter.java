package com.example.musicplayerapplication.presentation.view;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragemntList =new ArrayList<>();
    private List<String> titleList =new ArrayList<>();


    public PageAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
            return fragemntList.get(position);

    }

    @Override
    public int getCount() {
        return fragemntList.size();
    }



    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragemntList.add(fragment);
        titleList.add(title);
    }
}
