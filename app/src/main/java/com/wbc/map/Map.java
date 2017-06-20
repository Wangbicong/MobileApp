package com.wbc.map;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.wbc.kit.R;

public class Map extends AppCompatActivity {
    private MapView mapView;
    private AMap aMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setTitle(R.string.map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        findViewById(R.id.pos).setOnClickListener(v -> getLatlntPos());
        findViewById(R.id.describe).setOnClickListener(v -> getDescribePos());
        findViewById(R.id.current).setOnClickListener(v -> getCurrentPos());

        aMap = mapView.getMap();
        getCurrentPos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void moveTo(double x, double y) {
        CameraPosition pos = new CameraPosition(new LatLng(x, y), 16, 0, 0);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private void getLatlntPos() {
        EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
            .setTitle("请输入经纬度坐标：")
            .setIcon(R.drawable.remind)
            .setView(editText)
            .setNegativeButton("取消", null)
            .setPositiveButton("确定", (d, which) -> {
                String[] x = editText.getText().toString().split(" ");
                try {
                    moveTo(Double.valueOf(x[0]), Double.valueOf(x[1]));
                } catch (Exception e) {
                    Toast.makeText(this, "经纬度格式错误", Toast.LENGTH_SHORT).show();
                }})
            .show();
    }

    private void getDescribePos() {
        EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("请输入位置：")
                .setIcon(R.drawable.remind)
                .setView(editText)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", (d, which) -> {
                    GeocodeSearch geocodeSearch = new GeocodeSearch(this);
                    geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                        @Override
                        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

                        }

                        @Override
                        public void onGeocodeSearched(GeocodeResult result, int i) {
                            GeocodeAddress address = result.getGeocodeAddressList().get(0);
                            moveTo(address.getLatLonPoint().getLatitude(),
                                   address.getLatLonPoint().getLongitude());
                        }
                    });
                    GeocodeQuery query = new GeocodeQuery(editText.getText().toString(), "0451");
                    geocodeSearch.getFromLocationNameAsyn(query);
                })
                .show();
    }

    private void getCurrentPos() {
        AMapLocationClient client = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption option = new AMapLocationClientOption();

        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);

        client.setLocationOption(option);
        client.setLocationListener(location -> {
            if (location == null) return;
            if (location.getErrorCode() == 0) {
                moveTo(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this, "定位失败：" + location.getErrorInfo(), Toast.LENGTH_SHORT).show();
            }
        });

        client.startLocation();
    }
}