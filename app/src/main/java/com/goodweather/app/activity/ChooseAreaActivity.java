package com.goodweather.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.goodweather.app.R;
import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;
import com.goodweather.app.util.MyStringRequest;
import com.goodweather.app.util.ParseUtil;
import com.goodweather.app.util.VolleyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    @BindView(R.id.title_text)
    TextView mTitleText;
    @BindView(R.id.list_view)
    ListView mListView;

    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private GoodWeatherDB goodWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    //province,city,county list
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //selected province,city,county,level
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    //judge wether back from WeatherActivity
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getSupportActionBar().hide();
        setContentView(R.layout.choose_area);
        ButterKnife.bind(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(adapter);
        goodWeatherDB = GoodWeatherDB.getInstance(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(index).getCountyCode();
                    String weatherCode = new StringBuilder().append("101").append(countyCode).toString();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("weather_code", weatherCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    //query all province,firstly query in db,if faled query on server
    private void queryProvinces() {
        provinceList = goodWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText("全国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    //query all cities,firstly query from db,if faled query on server
    private void queryCities() {
        cityList = goodWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    //query all counties,firstly query from db,if faled query on server
    private void queryCounties() {
        countyList = goodWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    //query province/city/county data with incomed code and type
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();

        RequestQueue mQueue = VolleyUtil.getRequestQueue();
        MyStringRequest mRequest = new MyStringRequest(address, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("123456", response);
                boolean result = false;
                if ("province".equals(type)) {
                    result = ParseUtil.handleProvincesResponse(goodWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = ParseUtil.handleCitiesResponse(goodWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = ParseUtil.handleCountiesResponse(goodWeatherDB, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "Something wrong in Volley...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mQueue.add(mRequest);
    }

    //show progress dialog
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("查询中");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //close progress dialog
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //catch back button, judge which action should be act
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}
