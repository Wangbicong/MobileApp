package com.wbc.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wbc.kit.R;

/**
 * Created by wangbicong on 2017/6/24.
 */

public class Setting extends AppCompatActivity {

    EditText clientPortText;
    EditText serverIpText;
    EditText serverPortText;
    Button settingsButton;

    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clientPortText = (EditText)findViewById(R.id.client_port);
        serverIpText = (EditText)findViewById(R.id.server_ip);
        serverPortText = (EditText)findViewById(R.id.server_port);
        settingsButton = (Button)findViewById(R.id.settings_button);

        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

        clientPortText.setText(sp.getString("client_port", "0"));
        serverIpText.setText(sp.getString("server_ip", "0.0.0.0"));
        serverPortText.setText(sp.getString("server_port", "0"));

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("client_port", clientPortText.getText().toString());
                editor.putString("server_ip", serverIpText.getText().toString());
                editor.putString("server_port", serverPortText.getText().toString());
                editor.commit();
                Toast.makeText(Setting.this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}

