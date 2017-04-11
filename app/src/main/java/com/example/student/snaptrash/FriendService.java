package com.example.student.snaptrash;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class FriendService extends IntentService {


    public FriendService() {
        super("FriendService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_ADD_FRIEND)){
                String firstUserName = intent.getStringExtra("firstUserName");
                String secondUserName = intent.getStringExtra("secondUserName");
                Log.i("FriendService", "Service adding friend.");
                addFriends(firstUserName, secondUserName);
            }
            else if (action.equals(Constants.ACTION_SEND_FRIEND_REQUEST)){

                String toUser = intent.getStringExtra("toUser");
                String fromUser = intent.getStringExtra("fromUser");
                Log.i("DemoService", "Send friend request to " + toUser + " from " + fromUser);
                sendFriendRequest(fromUser, toUser);
            }
            else if (action.equals(Constants.ACTION_SEND_PHOTO)){
                String toUser = intent.getStringExtra("toUser");
                String fromUser = intent.getStringExtra("fromUser");
                Uri imageUri = intent.getParcelableExtra("imageUri");
                sendPhoto(fromUser, toUser, imageUri);
            }
        }
    }

    private void sendPhoto(String fromUser, String toUser, Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timestamp + "_.jpg";
            String imageDirectory = "sentPics";

            final SentPic sentPic = new SentPic();
            sentPic.setToUser(toUser);
            sentPic.setFromUser(fromUser);
            sentPic.setImageLocation(imageDirectory + "/" + imageFileName);

            Backendless.Files.Android.upload(
                    bitmap,
                    Bitmap.CompressFormat.JPEG,
                    100,
                    imageFileName,
                    imageDirectory,
                    new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(BackendlessFile response) {
                            //Log.i("sendPhoto", "Photo saved in Backendless!");
                            Backendless.Persistence.save(sentPic, new AsyncCallback<SentPic>() {
                                @Override
                                public void handleResponse(SentPic response) {

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    }
            );
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private void sendFriendRequest(final String fromUser, final String toUser){
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(String.format("name = '%s'", toUser));

        Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> response) {
                if (response.getData().size() == 0){
                    broadcastFriendRequestFailure();
                }else {
                    FriendRequest friendRequest = new FriendRequest();
                    friendRequest.setToUser(toUser);
                    friendRequest.setFromoUser(fromUser);
                    friendRequest.setAccepted(false);

                    Backendless.Persistence.save(friendRequest, new AsyncCallback<FriendRequest>() {
                        @Override
                        public void handleResponse(FriendRequest response) {
                            broadcastFriendRequestSuccess();
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            broadcastFriendRequestFailure();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                broadcastFriendRequestFailure();
            }
        });
    }

    private void addFriends(String firstUserName, String secondUserName){
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(String.format("name = '%s' or name = '%s'", firstUserName, secondUserName));
        Backendless.Persistence.of(BackendlessUser.class).find(query, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> response) {
                List<BackendlessUser> users = response.getData();
                if (users.size() != 2){
                    broadcastAddFriendFailure();
                }
                else {
                    BackendlessUser user1 = users.get(0);
                    final BackendlessUser user2 = users.get(1);

                    updateFriendList(user1, user2);
                    Backendless.UserService.update(user1, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser user) {
                            updateFriendList(user2, user);
                            Backendless.UserService.update(user2, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser response) {
                                    broadcastAddFriendSuccess();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    broadcastAddFriendFailure();
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            broadcastAddFriendFailure();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                broadcastAddFriendFailure();
            }
        });
    }
    private void broadcastAddFriendSuccess(){
        Intent intent = new Intent(Constants.BROADCAST_ADD_FRIEND_SUCCESS);
        sendBroadcast(intent);
    }
    private void broadcastAddFriendFailure(){
        Intent intent = new Intent(Constants.BROADCAST_ADD_FRIEND_FAILURE);
        sendBroadcast(intent);
    }

    private void broadcastFriendRequestSuccess(){
        Intent intent = new Intent(Constants.BROADCAST_FRIEND_REQUEST_SUCCESS);
        sendBroadcast(intent);
    }
    private void broadcastFriendRequestFailure(){
        Intent intent = new Intent(Constants.BROADCAST_FRIEND_REQUEST_FAILURE);
        sendBroadcast(intent);
    }

    private void updateFriendList(BackendlessUser user, BackendlessUser friend){
        BackendlessUser[] newFriends;

        Object[] currentFriendObjects = (Object[]) user.getProperty("friends");
        if (currentFriendObjects.length > 0){
            BackendlessUser[] currentFriends = (BackendlessUser[]) currentFriendObjects;
            newFriends = new BackendlessUser[currentFriends.length + 1];
            for (int i = 0; i < currentFriends.length; i++){
                newFriends[i] = currentFriends[i];
            }
            newFriends[newFriends.length - 1] = friend;
        }
        else {
            newFriends = new BackendlessUser[]{
                    friend
            };
        }
        user.setProperty("friends", newFriends);
    }


}
