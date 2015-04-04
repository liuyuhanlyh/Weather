package com.lyh.weather.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.lyh.weather.bean.City;
import com.lyh.weather.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyuhan on 15/3/27.
 */
public class MyApplication extends Application{
    private static final String TAG = "MyAPP";
    private static Application mApplication;
    private List<City> mCityList;
    private static CityDB mCityDB;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "MyApplication->Oncreate");
        mApplication = this;

        mCityDB = openCityDB();
        //initCityList();
    }

    public static Application getInstance(){
        return mApplication;
    }
    public static CityDB getCityDB(){
        return mCityDB;
    }

    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d("Dir:", path);

        if (!db.exists()){
            Log.i("MyApp", "db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                db.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();;
                is.close();
            }catch (IOException e){
                e.printStackTrace();;
                System.exit(0);
            }
        }
        return  new CityDB(this, path);
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //prepareCityList();
                mCityList = mCityDB.getAllCity();
            }
        }).start();
    }

    private boolean prepareCityList(){
        mCityList = mCityDB.getAllCity();
        for (City city : mCityList){
            String cityName = city.getCity();
            Log.d(TAG, cityName);
        }
        return true;
    }
}
