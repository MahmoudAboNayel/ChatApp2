package com.example.abo_nayel.chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Abo-Nayel on 11/02/2018.
 */

class SectionsPageAdapter extends FragmentPagerAdapter{

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                RequestsFragment requestsFragment= new RequestsFragment();
                return requestsFragment;
            case 0:
                ChatsFragment chatsFragment= new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment= new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}
