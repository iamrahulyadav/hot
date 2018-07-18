package com.hotactress.hot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.UsersActivity;
import com.hotactress.hot.adapters.RequestAdapter;
import com.hotactress.hot.models.Friend;
import com.hotactress.hot.models.Request;
import com.hotactress.hot.utils.Gen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubhamagrawal on 17/07/18.
 */

public class RequestsFragment extends Fragment {


    private RecyclerView mRequestList;
    private static final String TAG = FriendsFragment.class.getSimpleName();

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;
    private View mMainView;
    private List<Request> requestList;
    RequestAdapter requestAdapter;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList, mCurrent_user_id, getActivity());

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRequestList.setAdapter(requestAdapter);

        View.OnClickListener goToAllUsersActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(getActivity(), false, UsersActivity.class );
            }
        };

        mMainView.findViewById(R.id.request_no_result_layout).setOnClickListener(goToAllUsersActivity);

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Request> newRequestList = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String friendKey = child.getKey();
                    Request request = child.getValue(Request.class);
                    request.setId(friendKey);
                    newRequestList.add(request);
                }

                // notify data change
                requestList.removeAll(requestList);
                requestList.addAll(newRequestList);
                if(requestList.size() == 0) {
                    mMainView.findViewById(R.id.request_no_result_layout).setVisibility(View.VISIBLE);
                    mRequestList.setVisibility(View.GONE);
                } else {
                    mRequestList.setVisibility(View.VISIBLE);
                    mMainView.findViewById(R.id.request_no_result_layout).setVisibility(View.GONE);
                }
                requestAdapter.notifyDataSetChanged();
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