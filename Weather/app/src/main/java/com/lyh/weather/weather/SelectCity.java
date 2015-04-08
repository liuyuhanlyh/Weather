package com.lyh.weather.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.text.TextWatcher;
import android.text.TextUtils;
import android.text.Editable;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.text.InputType;

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
    private List<String> newCityData;
    private static String cityNum;
    //private String[] cityData = {"北京","上海","重庆","天津","拉萨","成都","长沙","厦门","深圳","珠海","长春"};
    private ArrayAdapter<String>adapter;
    private ArrayAdapter<String>newAdapter;
    private EditText mSearchBar;
    private Drawable mIconSearchDefault; // 搜索文本框默认图标
    private Drawable mIconSearchClear; // 搜索文本框清除文本内容图标
    private TextWatcher search_TextChanged = new TextWatcher() {

        //缓存上一次文本框内是否为空
        private boolean isnull = true;

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                if (!isnull) {
                    mSearchBar.setCompoundDrawablesWithIntrinsicBounds(null,
                            null, mIconSearchDefault, null);
                    isnull = true;
                }
            } else {
                if (isnull) {
                    mSearchBar.setCompoundDrawablesWithIntrinsicBounds(null,
                            null, mIconSearchClear, null);
                    isnull = false;
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        /**
         * 随着文本框内容改变动态改变列表内容
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            newCityData.clear();
            if (mSearchBar.getText() != null){
                String input_info = mSearchBar.getText().toString().trim();
                newCityData = getNewCityData(input_info);
                newAdapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1,newCityData);
                mListView.setAdapter(newAdapter);
            }
        }
    };


    private OnTouchListener txtSearch_OnTouch = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    int curX = (int) event.getX();
                    if (curX > v.getWidth() - 38 && !TextUtils.isEmpty(mSearchBar.getText())) {
                        mSearchBar.setText("");
                        int cacheInputType = mSearchBar.getInputType();// backup  the input type
                        mSearchBar.setInputType(InputType.TYPE_NULL);// disable soft input
                        mSearchBar.onTouchEvent(event);// call native handler
                        mSearchBar.setInputType(cacheInputType);// restore input  type
                        return true;// consume touch even
                    }
                    break;
            }
            return false;
        }
    };

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

        final Resources res = getResources();
        mIconSearchDefault = res.getDrawable(R.drawable.magnifying_glass);
        mIconSearchClear = res.getDrawable(R.drawable.search_clear);

        mSearchBar = (EditText) findViewById(R.id.search_edit);
        mSearchBar.addTextChangedListener(search_TextChanged);
        mSearchBar.setOnTouchListener(txtSearch_OnTouch);
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
        newCityData = new ArrayList<String>();
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
            newCityData.add(cityName);
        }

//        for (String cityName : cityData){
//            Log.d("CityName2", cityName);
//        }

        return true;
    }

    private List<String> getNewCityData(String input_info){
        for (int i=0;i<cityData.size();i++){
            String cityName = cityData.get(i);
            if (cityName.contains(input_info)){
                newCityData.add(cityName);
                Log.d("searchResult",cityName);
            }
        }
        return newCityData;
    }
}
