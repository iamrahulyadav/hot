package com.hotactress.hot.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hotactress.hot.fragments.ActivitiesFragment;
import com.hotactress.hot.fragments.ChatsFragment;
import com.hotactress.hot.fragments.FriendsFragment;
import com.hotactress.hot.fragments.RequestsFragment;

/**
 * Created by shubhamagrawal on 17/07/18.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    RequestsFragment requestsFragment ;
    ChatsFragment chatsFragment ;
    FriendsFragment friendsFragment ;
    ActivitiesFragment activitiesFragment ;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        requestsFragment = new RequestsFragment();
        chatsFragment = new ChatsFragment();
        friendsFragment = new FriendsFragment();
        activitiesFragment = new ActivitiesFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                return requestsFragment;

            case 1:
                return  chatsFragment;

            case 2:
                return friendsFragment;
            case 3:
                return activitiesFragment;
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "REQUESTS";

            case 1:
                return "CHATS";

            case 2:
                return "FRIENDS";

            case 3:
                return "ACTIVITIES";

            default:
                return null;
        }

    }

}
