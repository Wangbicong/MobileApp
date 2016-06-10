package com.ylxdzsw.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ylxdzsw.kit.R;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Tuple<T,S> {
    public final T first;
    public final S rest;

    Tuple(T first, S rest) {
        this.first = first;
        this.rest  = rest;
    }
}

public class Chat extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private ListView listView;
    private Socket socket;
    private ArrayList<Tuple<String, String>> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);
        button   = (Button)   findViewById(R.id.button);
        data     = new ArrayList<>();

        button.setOnClickListener(v -> {
            if (socket == null) {
                connect();
            } else {
                send();
            }
        });
        listView.setAdapter(new ChatAdapter(this));

    }

    private void send() {}

    private void connect() {
        data.add(new Tuple<>("fuck", editText.getText().toString()));
        editText.setText("");
    }

    class ChatAdapter extends ArrayAdapter<Tuple<String, String>> {
        public ChatAdapter(Context context) {
            super(context, 0, data);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Tuple<String, String> data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText(data.first + ": " + data.rest);
            return convertView;
        }
    }
}
