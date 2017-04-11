package com.example.student.snaptrash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class FriendReciever extends BroadcastReceiver {
    public FriendReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.BROADCAST_ADD_FRIEND_SUCCESS)){
            Toast.makeText(context, "Added Friend!", Toast.LENGTH_SHORT).show();

        }
        else if (action.equals(Constants.BROADCAST_ADD_FRIEND_FAILURE)){
            Toast.makeText(context, "Fail To Add Friend!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Constants.BROADCAST_FRIEND_REQUEST_SUCCESS)){
            Toast.makeText(context, "Sent Request", Toast.LENGTH_SHORT).show();
        }
        else if (action.equals(Constants.BROADCAST_FRIEND_REQUEST_FAILURE)){
            Toast.makeText(context, "Failed to send Request", Toast.LENGTH_SHORT).show();
        }
    }
}
