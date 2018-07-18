package com.hotactress.hot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.UsersActivity;
import com.hotactress.hot.adapters.FriendsAdapter;
import com.hotactress.hot.adapters.UserListAdapter;
import com.hotactress.hot.models.Friend;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shubhamagrawal on 17/07/18.
 */

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;
    private static final String TAG = FriendsFragment.class.getSimpleName();

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private View mMainView;
    private List<Friend> friendList;
    FriendsAdapter friendsAdapter;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        friendList = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friendList, getActivity());

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendsList.setAdapter(friendsAdapter);

        View.OnClickListener goToAllUsersActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(getActivity(), false, UsersActivity.class );
            }
        };

        mMainView.findViewById(R.id.friend_no_result_layout).setOnClickListener(goToAllUsersActivity);

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Friend> newFriendList = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendKey = child.getKey();
                    Friend friend = child.getValue(Friend.class);
                    friend.setId(friendKey);
                    newFriendList.add(friend);
                }

                // notify data change
                friendList.removeAll(friendList);
                friendList.addAll(newFriendList);
                if(friendList.size() == 0) {
                    mMainView.findViewById(R.id.friend_no_result_layout).setVisibility(View.VISIBLE);
                    mFriendsList.setVisibility(View.GONE);
                } else {
                    mFriendsList.setVisibility(View.VISIBLE);
                    mMainView.findViewById(R.id.friend_no_result_layout).setVisibility(View.GONE);
                }
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        // Inflate the layout for this fragment
        return mMainView;
    }
}