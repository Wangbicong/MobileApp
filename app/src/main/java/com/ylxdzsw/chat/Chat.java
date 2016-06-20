package com.ylxdzsw.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
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
        String msg = editText.getText().toString();

        new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.println(msg);
                writer.flush();
                data.add(new Tuple<>("我", msg));
                runOnUiThread(() -> ((ChatAdapter) listView.getAdapter()).notifyDataSetChanged());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show());
                return;
            }
        }).start();
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
            } catch (Exception e) {
                runOnUiThread(() ->  Toast.makeText(this, "地址有误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void listen() {
        socket = null;
        data.clear();
        runOnUiThread(() -> button.setText("连接"));

        try {
            Enumeration interfaceIter = NetworkInterface.getNetworkInterfaces();
            while(interfaceIter.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) interfaceIter.nextElement();
                Enumeration addressIter = n.getInetAddresses();
                while (addressIter.hasMoreElements())
                {
                    InetAddress i = (InetAddress) addressIter.nextElement();
                    data.add(new Tuple<>(i.getHostAddress(), String.valueOf(getResources().getInteger(R.integer.listen_port))));
                }
            }
            runOnUiThread(() -> ((ChatAdapter) listView.getAdapter()).notifyDataSetChanged());
        } catch (SocketException e) {
            Toast.makeText(this, "监听失败", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(getResources().getInteger(R.integer.listen_port));
                socket = serverSocket.accept();
                serverSocket.close();
                receive();
            } catch (SocketException e) {

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "监听失败", Toast.LENGTH_SHORT).show());
                return;
            }
        }).start();
    }

    private void receive() {
        data.clear();
        runOnUiThread(() -> button.setText("发送"));

        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        runOnUiThread(() -> Toast.makeText(this, "对方终止了连接", Toast.LENGTH_SHORT).show());
                        listen();
                        return;
                    }
                    data.add(new Tuple<>("对方", line));
                    runOnUiThread(() -> ((ChatAdapter) listView.getAdapter()).notifyDataSetChanged());
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "读取失败", Toast.LENGTH_SHORT).show());
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
