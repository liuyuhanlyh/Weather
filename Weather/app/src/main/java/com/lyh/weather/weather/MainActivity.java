package com.lyh.weather.weather;

import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.lyh.weather.R;
import com.lyh.weather.bean.TodayWeather;
import com.lyh.weather.util.NetUtil;
import com.lyh.weather.util.PinYin;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;
import java.lang.reflect.Field;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private ImageView mUpdateBtn;
    private ImageView mCityBtn;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, climateTv, windTv;
    private ImageView weatherImg, pmImg;
    private TodayWeather todayWeather;
    private String cityCode;

    private static final int UPDATE_TODAY_WEATHER = 1;
    private Handler mHandler = new Handler(){
       @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MyApp", "MainActivity->Oncreate");

        setContentView(R.layout.weather_info);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mCityBtn = (ImageView) findViewById(R.id.title_city_manager);
        mCityBtn.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        cityCode = sharedPreferences.getString("main_city_code", "101010100");
        initView();
    }

    void initView(){
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);

        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    void updateTodayWeather(TodayWeather todayWeather){
        int pm25 = Integer.parseInt(todayWeather.getPm25().trim());;
        String pmImgStr = "0_50";
        String highTem = todayWeather.getHigh();
        String lowTem = todayWeather.getLow();

        Log.d("myapp3", todayWeather.toString());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度:" +todayWeather.getShidu());
        weekTv.setText(todayWeather.getDate());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        temperatureTv.setText(highTem.substring(highTem.lastIndexOf(" ")+1)+"~"+lowTem.substring(lowTem.lastIndexOf(" ")+1));
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());


        if ((pm25 >= 51) && (pm25 <= 200)){
            int startV = (pm25 - 1)/50*50 +1;
            int endV = ((pm25 - 1) / 50 + 1) * 50;
            pmImgStr = Integer.toString(startV) + "_" + endV;
        }
        else if ((pm25 >= 201) && (pm25 <= 300)){
            pmImgStr = "201_300";
        }
        else if (pm25 >= 301){
            pmImgStr = "greater_300";
        }

        String typeImg = "biz_plugin_weather_" + PinYin.convertToSpell(todayWeather.getType());
        Class aClass = R.drawable.class;
        int typeId = -1;
        int pmImgId = -1;
        try{
            Field field = aClass.getField(typeImg);
            Object value = field.get(new Integer(0));
            typeId = (int)value;
            Field pmField = aClass.getField("biz_plugin_weather_" + pmImgStr);
            Object pmImgO = pmField.get(new Integer(0));
            pmImgId = (int) pmImgO;
        }catch (Exception e){
            if ( -1 == typeId) {
                typeId = R.drawable.biz_plugin_weather_qing;
            }
            if ( -1 == pmImgId) {
                pmImgId = R.drawable.biz_plugin_weather_0_50;
            }
        }finally {
            Drawable drawable = getResources().getDrawable(typeId);
            weatherImg.setImageDrawable(drawable);
            drawable = getResources().getDrawable(pmImgId);
            pmImg.setImageDrawable(drawable);
            Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.title_update_btn){
            Log.d("citycode1",cityCode);
//            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
//            cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("citycode2",cityCode);

            Log.d("myWeather cityCode",cityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
        else if (view.getId() == R.id.title_city_manager){
            Log.d("intent1", "OK");
            Intent intent=new Intent(MainActivity.this,SelectCity.class);
//            startActivity(intent);
            startActivityForResult(intent, 1);
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        HttpEntity entity = httpResponse.getEntity();
                        InputStream responseStream = entity.getContent();
                        responseStream = new GZIPInputStream(responseStream);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder response = new StringBuilder();
                        String str;
                        while((str = reader.readLine()) != null){
                            response.append(str);
                        }
                        String responseStr = response.toString();
                        Log.d("myWeather",responseStr);
                        todayWeather = parseXML(responseStr);
                        if (todayWeather != null){
                            Log.d("myapp", todayWeather.toString());
                            Message msg = new Message();
                            msg.what = UPDATE_TODAY_WEATHER;
                            msg.obj = todayWeather;
                            mHandler.sendMessage(msg);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        try{
            int fengxiangCount = 0;
            int fengliCount = 0;
            int dateCount = 0;
            int highCount = 0;
            int lowCount = 0;
            int typeCount = 0;

            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }

                        if (todayWeather != null){
                            if (xmlPullParser.getName().equals("city")){
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("updatetime")){
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("shidu")){
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("wendu")){
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("pm25")){
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("quality")){
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if (xmlPullParser.getName().equals("fengli") && fengliCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if (xmlPullParser.getName().equals("date") && dateCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }
                            else if (xmlPullParser.getName().equals("high") && highCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }
                            else if (xmlPullParser.getName().equals("low") && lowCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }
                            else if (xmlPullParser.getName().equals("type") && typeCount==0){
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return todayWeather;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK){
            cityCode = data.getStringExtra("name");
        }
    }
}
