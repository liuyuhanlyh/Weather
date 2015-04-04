package com.lyh.weather.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.lyh.weather.R;
import com.lyh.weather.app.MyApplication;
import com.lyh.weather.bean.City;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyuhan on 15/3/30.
 */
public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mListView;
    private List<City> mCityList;
    private List<String> cityData;
    private static String cityNum;
    //private String[] cityData = {"北京","上海","重庆","天津","拉萨","成都","长沙","厦门","深圳","珠海","长春"};
    ArrayAdapter<String>adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.city_list_view);
        cityNum = "";
        initCityList();

//        for (String cityName : cityData){
//            Log.d("CityName2", cityName);
//        }
        adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1,cityData);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cityNum = mCityList.get(i).getNumber().toString().trim();
                Log.d("CityNum", cityNum);
                Intent mIntent = new Intent();
                mIntent.putExtra("name", cityNum);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.title_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        cityData = new ArrayList<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList(){
        mCityList = MyApplication.getCityDB().getAllCity();
        for (City city : mCityList){
            String cityName = city.getCity();
            cityData.add(cityName);
        }

//        for (String cityName : cityData){
//            Log.d("CityName2", cityName);
//        }

        return true;
    }
}
