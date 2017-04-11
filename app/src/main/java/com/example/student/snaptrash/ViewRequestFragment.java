package com.example.student.snaptrash;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRequestFragment extends Fragment {

    private ArrayList<String> fromUsers;
    private ArrayList<FriendRequest> friendRequests;
    private ArrayAdapter<String> friendRequestAdapter;

    public ViewRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_request, container, false);

        fromUsers = new ArrayList<String>();
        friendRequests = new ArrayList<FriendRequest>();
        friendRequestAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, fromUsers);

        ListView friendList = (ListView) view.findViewById(R.id.viewRequest);
        friendList.setAdapter(friendRequestAdapter);

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAcceptDialog(position);

            }
        });


        String userId = Backendless.UserService.loggedInUser();
        Backendless.UserService.findById(userId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                String currentUserName = response.getProperty("name").toString();
                getIncomingFriendRequest(currentUserName);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        return view;
    }
    private void showAcceptDialog(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Accept friend request from " + fromUsers.get(position) + "?");

        dialog.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                acceptRequest(friendRequests.get(position));
            }
        });
        dialog.create();
        dialog.show();
    }

    private void acceptRequest(final FriendRequest request){
        request.setAccepted(true);
        Backendless.Persistence.save(request, new AsyncCallback<FriendRequest>() {
            @Override
            public void handleResponse(FriendRequest response) {
                Intent intent = new Intent(getActivity(), FriendService.class);
                intent.setAction(Constants.ACTION_ADD_FRIEND);
                intent.putExtra("firstUserName", request.getFromUser());
                intent.putExtra("secondUserName", request.getToUser());
                getActivity().startService(intent);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    private void getIncomingFriendRequest(String username){
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(String.format("toUser = '%s'", username));
        Backendless.Persistence.of(FriendRequest.class).find(query, new AsyncCallback<BackendlessCollection<FriendRequest>>() {
            @Override
            public void handleResponse(BackendlessCollection<FriendRequest> response) {
                List<FriendRequest> incomingRequests = response.getData();
                for (FriendRequest request : incomingRequests){
                    if (!request.isAccepted()){
                        fromUsers.add(request.getFromUser());
                        friendRequests.add(request);
                    }
                }
                friendRequestAdapter.notifyDataSetChanged();
            }


            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

}
