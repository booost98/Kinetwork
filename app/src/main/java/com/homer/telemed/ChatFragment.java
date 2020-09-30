package com.homer.telemed;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private EditText myMessage;
    private ImageButton sendButton;
    private ListView messages_view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        MainActivity.hideKeyboard(getActivity());


        myMessage = view.findViewById(R.id.myMessage);
        sendButton = view.findViewById(R.id.sendButton);
        messages_view = (ListView) view.findViewById(R.id.messages_view);
        final List<String> messages = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.my_message, messages);
        messages_view.setAdapter(arrayAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = myMessage.getText().toString();
                if(message.length()>0){
                    messages.add(message);
                    arrayAdapter.notifyDataSetChanged();
                    myMessage.getText().clear();
                }
            }
        });

        return view;
    }


}
