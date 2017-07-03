package com.wbc.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wbc.kit.R;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Chat extends AppCompatActivity {

    private static final int RECEIVE_MESSAGE = 0;
    private static final int SEND_MESSAGE = 1;

    private TextView textView;
    private EditText editText;
    private Button button;

    // 展示消息用的ListView
    private ListView lvMsg;
    // 相应的适配器
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();

    // 本机
    private static String thisIP;
    private int thisPort;
    // 通信对方
    private String thatIP;
    private int thatPort;

    private ServerListener serverListener;
    private ClientSend clientSend;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message meg) {
            switch (meg.what) {
                case Chat.RECEIVE_MESSAGE:
                    // 添加一个"接收"类型的消息
                    Msg msg = new Msg((String)meg.obj, Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    break;
                case Chat.SEND_MESSAGE:
                    Msg msg1 = new Msg((String)meg.obj, Msg.TYPE_SENT);
                    msgList.add(msg1);
                    adapter.notifyDataSetChanged();
                    lvMsg.setSelection(msgList.size());
                    editText.setText("");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        thisPort = Integer.parseInt(sp.getString("client_port", "0"));
        thatIP = sp.getString("server_ip", "0.0.0.0");
        thatPort = Integer.parseInt(sp.getString("server_port", "0"));

        textView = (TextView)findViewById(R.id.chat_ip_text);
        lvMsg = (ListView)findViewById(R.id.chat_listView);
        editText = (EditText)findViewById(R.id.chat_editText);
        button = (Button)findViewById(R.id.chat_button);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        thisIP = intToIp(ipAddress);

        try{
            serverListener = new ServerListener(thisPort);
            serverListener.start();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "开启服务器错误",
                    Toast.LENGTH_SHORT).show();
        }

        adapter = new MsgAdapter(Chat.this, R.layout.msg_item, msgList);
        lvMsg.setAdapter(adapter);

        textView.setText("本地信息： "+thisIP+":"+thisPort);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if(!"".equals(content)) {
                    try{
                        clientSend = new ClientSend(thatIP, thatPort, content);
                        clientSend.start();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), "发送错误",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * 客户端通过Socket发送消息的线程
     */
    class ClientSend extends Thread {
        private String serverIP;
        private int serverPort;
        private String content;
        private Socket clientSocket;

        public ClientSend(String ip, int port, String content) {
            super();
            this.serverIP = ip;
            this.serverPort = port;
            this.content = content;
        }

        @Override
        public void run() {
            try{

                // 实例化一个Socket
                clientSocket = new Socket(this.serverIP,this.serverPort);
                // 打开OutputStream
                OutputStream outputStream = clientSocket.getOutputStream();
                // 写数据，完成发送
                outputStream.write(this.content.getBytes());

                // 添加一个"发送"类型消息的展示（如果Socket发送成功的话，不然会报错）
                Message message = new Message();
                message.what = Chat.SEND_MESSAGE;
                message.obj = content;
                handler.sendMessage(message);

            }catch (IOException e) {
                Looper.prepare();
                // 发送失败，显示原因
                Toast.makeText(Chat.this,"发送失败: \n"+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }
    }

    /**
     * Socket通信服务端监听线程
     */
    class ServerListener extends Thread {
        private int serverPort;

        public ServerListener(int port) {
            super();
            this.serverPort = port;
        }

        @Override
        public void run() {

            try {
                // 实例化一个ServerSocket，监听特定端口
                ServerSocket serverSocket = new ServerSocket(serverPort);

                while(true) {
                    // ServerSocket监听阻塞
                    Socket socket = serverSocket.accept();

                    // 接收到通信体后，赋给socket，读出其中的数据
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] bytes = new byte[1024];
                    dataInputStream.read(bytes);
                    String receive = "";
                    receive = new String(bytes, StandardCharsets.UTF_8);

                    dataInputStream.close();

                    // receive中存储着接收到的消息
                    if (!"".equals(receive)) {
                        // 添加一个"接收"类型的消息
                        Message message = new Message();
                        message.what = Chat.RECEIVE_MESSAGE;
                        message.obj = receive;
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                Looper.prepare();
                Toast.makeText(Chat.this,"绑定失败: \n"+e.toString(),Toast.LENGTH_LONG).show();
                Looper.loop();
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        if(this.serverListener != null) {
            serverListener.interrupt();
            serverListener = null;
        }
        if(this.clientSend != null) {
            clientSend.interrupt();
            clientSend = null;
        }
        super.onDestroy();
    }


    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

}

class Msg {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;

    public Msg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}

class MsgAdapter extends ArrayAdapter<Msg> {
    private int resourceId;

    public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Msg msg = getItem(position);
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            holder = new ViewHolder();
            holder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            holder.rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            holder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
            holder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }
        if(msg.getType() == Msg.TYPE_RECEIVED) {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SENT) {
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
        return view;
    }


    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
    }

}
