package com.example.student.snaptrash;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Objects;


public class FriendsFragment extends Fragment {

    private ArrayList<String> friends;
    private ArrayAdapter<String> friendListAdapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        final Uri imageToSend = getActivity().getIntent().getParcelableExtra("ImageURI");

        Button button = (Button) view.findViewById(R.id.friendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Add a Friend.");

                final EditText inputField = new EditText(getActivity());
                alertDialog.setView(inputField);

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendFriendRequest(inputField.getText().toString());
                        //Toast.makeText(getActivity(), "Add Friend!", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.create();
                alertDialog.show();
            }
        });
        friends = new ArrayList<String>();
        friendListAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, friends);

        final ListView friendsList = (ListView) view.findViewById(R.id.friends);
        friendsList.setAdapter(friendListAdapter);

        String currentUser = Backendless.UserService.loggedInUser();
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                Object[] friendObjects = (Object[]) user.getProperty("friends");
                if (friendObjects.length > 0) {
                    BackendlessUser[] friendArray = (BackendlessUser[]) friendObjects;
                    for (BackendlessUser friend : friendArray) {
                        String name = friend.getProperty("name").toString();
                        friends.add(name);
                        friendListAdapter.notifyDataSetChanged();
                    }
                }
                final String currentUserName = (String)user.getProperty("name");
                friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String friendName = (String) parent.getItemAtPosition(position);
                        sendImageToFriend(currentUserName,friendName, imageToSend);
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });


        return view;
    }

    private void sendImageToFriend(String currentUser, String toUser, Uri imageURI){
        Intent intent = new Intent(getActivity(), FriendService.class);
        intent.setAction(Constants.ACTION_SEND_PHOTO);
        intent.putExtra("fromUser", currentUser);
        intent.putExtra("toUser", toUser);
        intent.putExtra("imageUri", imageURI);
        getActivity().startService(intent);
    }

    private void sendFriendRequest(final String friendName){
        final String currentUserId = Backendless.UserService.loggedInUser();
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser currUser) {
                Intent intent = new Intent(getActivity(), FriendService.class);
                intent.setAction(Constants.ACTION_SEND_FRIEND_REQUEST);
                intent.putExtra("fromUser", currUser.getProperty("name").toString());
                intent.putExtra("toUser", friendName);


                getActivity().startService(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });


    }


}
