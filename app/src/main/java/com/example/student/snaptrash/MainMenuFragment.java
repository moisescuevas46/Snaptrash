package com.example.student.snaptrash;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.util.IllegalFormatCodePointException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {

    private static int REQUEST_CHOOSE_PHOTO = 2;

    public MainMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu,container,false);

        String[] menuItems = {"Camera", "Login/Logout", "Friends", "Gallery", "Register", "Friend Requests", "Incoming Photos"};

        ListView listView = (ListView) view.findViewById(R.id.mainMenu);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,menuItems);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent(getActivity(), Camera.class);
                    startActivity(intent);
                }
                else if (position == 1){
                    Intent intent = new Intent(getActivity(), LoginMenuActivity.class);
                    startActivity(intent);
                }
                else if (position == 2){
                    Intent intent = new Intent(getActivity(), Friends.class);
                    startActivity(intent);
                }
                else if (position == 3){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_PHOTO);
                }
                else if (position == 4){
                    Intent intent = new Intent(getActivity(), Register.class);
                    startActivity(intent);
                }
                else if (position == 5){
                    Intent intent = new Intent(getActivity(), ViewRequest.class);
                    startActivity(intent);
                }
                else if (position == 6){
                    Intent intent = new Intent(getActivity(), InboxActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_PHOTO){
            if (resultCode == Activity.RESULT_OK){
                Uri uri = data.getData();
                Intent intent = new Intent(getActivity(), Friends.class);
                intent.putExtra("ImageURI", uri);
                startActivity(intent);
            }
        }
    }
}
