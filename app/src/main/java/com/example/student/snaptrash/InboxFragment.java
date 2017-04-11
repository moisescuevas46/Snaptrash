package com.example.student.snaptrash;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    private List<String> fromFriends;
    private List<SentPic> incomingPhotos;
    private ArrayAdapter<String> fromFriendsAdapter;


    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        fromFriends = new ArrayList<String>();
        fromFriendsAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, fromFriends);
        incomingPhotos = new ArrayList<SentPic>();

        final ListView friendList = (ListView) view.findViewById(R.id.incomingPhotos);
        friendList.setAdapter(fromFriendsAdapter);

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayImageFromFriend(position);
            }
        });

        final String currentUser = Backendless.UserService.loggedInUser();
        Backendless.Persistence.of(BackendlessUser.class).findById(currentUser, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                String currentUserName = (String) user.getProperty("name");
                getPhotosSentTo(currentUserName);

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });



        return view;
    }
    private void displayImageFromFriend(int position){
        String imageLocation = incomingPhotos.get(position).getImageLocation();
        try {
            URL url = new URL("https://api.backendless.com/E85AC4AC-ADB7-F784-FFCD-8EA3954F3300/v1/files/" + imageLocation);
            DownloadFilesTask task = new DownloadFilesTask();
            task.execute(url);
        }
        catch (MalformedURLException e){
            e.printStackTrace();

        }



    }
    private void getPhotosSentTo(String username){
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(String.format("toUser = '%s'", username));

        Backendless.Persistence.of(SentPic.class).find(query, new AsyncCallback<BackendlessCollection<SentPic>>() {
            @Override
            public void handleResponse(BackendlessCollection<SentPic> response) {
                List<SentPic> photos = response.getData();
                for (SentPic photo : photos){
                    if (!photo.isViewed()){
                        fromFriends.add(photo.getFromUser());

                        incomingPhotos.add(photo);
                    }
                }
                fromFriendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }
    private void displayPopupImage(Bitmap bitmap){
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity());
        imageDialog.setMessage("Incoming Photo");

        imageDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        ImageView imageView = new ImageView(getActivity());
        imageView.setImageBitmap(bitmap);
        imageDialog.setView(imageView);

        imageDialog.create();
        imageDialog.show();
    }


    private class DownloadFilesTask extends AsyncTask<URL, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(URL... params) {
            for (URL url: params){
                try {
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    int responseCode = httpCon.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = httpCon.getInputStream();
                        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        return imageBitmap;
                    }
                }
                catch (IOException e){
                    e.printStackTrace();

                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            displayPopupImage(bitmap);

        }
    }

}
