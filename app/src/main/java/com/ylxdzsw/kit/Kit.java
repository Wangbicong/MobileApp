package com.ylxdzsw.kit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class App {
    public String name;
    public int icon;
    public Class activity;

    public App(String name, int icon, Class activity) {
        this.name     = name;
        this.icon     = icon;
        this.activity = activity;
    }

    static App[] appList = {
        new App("计算器", R.drawable.calculator, com.ylxdzsw.calculator.Calculator.class),
        new App("弹球",   R.drawable.pinball,    com.ylxdzsw.pinball.Pinball.class),
        new App("聊天",   R.drawable.chat,       com.ylxdzsw.calculator.Calculator.class),
        new App("地图",   R.drawable.map,        com.ylxdzsw.calculator.Calculator.class)
    };
}

public class Kit extends AppCompatActivity {
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        gridView = (GridView) findViewById(R.id.gridView);

        initGridView();
    }

    private void initGridView() {
        List<Map<String, Object>> data = new ArrayList<>();

        for (App app : App.appList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name",     app.name);
            map.put("icon",     app.icon);
            map.put("activity", app.activity);
            data.add(map);
        }

        gridView.setAdapter(new SimpleAdapter(this, data, R.layout.kit_item,
                new String[] {"name", "icon"},
                new int[] {R.id.text, R.id.image}));

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> item = (HashMap<String, Object>) parent.getAdapter().getItem(position);
            startActivity(new Intent(this, (Class) item.get("activity")));
        });
    }
}
