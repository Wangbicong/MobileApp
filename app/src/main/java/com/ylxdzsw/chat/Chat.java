package com.ylxdzsw.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.ylxdzsw.kit.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
    private ServerSocket serverSocket;
    private ArrayList<Tuple<String, String>> data;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.listView);
        button   = (Button)   findViewById(R.id.button);
        data     = new ArrayList<>();
        handler  = new Handler();

        button.setOnClickListener(v -> {
            if (socket == null) {
                String[] addr = editText.getText().toString().split(":");
                if (addr.length != 2) {
                    Toast.makeText(this, "地址有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                connect(new Tuple<>(addr[0], addr[1]));
            } else {
                send();
            }
            editText.setText("");
        });
        listView.setAdapter(new ChatAdapter(this));

        listen();
    }

    private void send() {
        String data = editText.getText().toString();

        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.println(data);
        } catch (Exception e) {
            return;
        }
    }

    private void connect(Tuple<String, String> addr) {
        String ip = addr.first;
        int port  = Integer.parseInt(addr.rest);

        new Thread(() -> {
            try {
                InetAddress address = InetAddress.getByName(ip);
                socket = new Socket(address, port);
                serverSocket.close();
                receive();
            } catch (SocketException e) {

            } catch (Exception e) {
                Toast.makeText(this, "地址有误", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    private void listen() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(getResources().getInteger(R.integer.listen_port));
                socket = serverSocket.accept();
                serverSocket.close();
                receive();
            } catch (IOException e) {
                return;
            }
        }).start();
    }

    private void receive() {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(new Tuple<>("对方", line));
                }
            } catch (IOException e) {
                return;
            }
        }).start();
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
