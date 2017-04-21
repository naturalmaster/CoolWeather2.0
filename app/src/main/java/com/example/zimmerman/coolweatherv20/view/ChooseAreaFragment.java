package com.example.zimmerman.coolweatherv20.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zimmerman.coolweatherv20.R;
import com.example.zimmerman.coolweatherv20.db.City;
import com.example.zimmerman.coolweatherv20.db.County;
import com.example.zimmerman.coolweatherv20.db.Province;
import com.example.zimmerman.coolweatherv20.gson.Weather;
import com.example.zimmerman.coolweatherv20.util.HttpUtil;
import com.example.zimmerman.coolweatherv20.util.Utility;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Zimmerman on 2017/4/13.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ListView listView;
    private ProgressDialog progressDialog;
    private Button backButton;
    private TextView titleText;
    private ArrayAdapter arrayAdapter;
    private List<String> data = new ArrayList<>();

    private List<City> cityList;
    private List<Province> provinceList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area_fragment, null);
        listView = (ListView) view.findViewById(R.id.list_vew_choose);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                } else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();

                    if (getActivity() instanceof MainActivity) {
                        //跳转到天气显示界面
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString("weather_id",weatherId);
                        editor.apply();
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString("weather_id",weatherId);
                        editor.apply();
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawer(GravityCompat.START);
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

        queryProvinces();
    }

    private void queryFromServer(final String type) {
        showProgressDialog();
        /**
         * 设置地址
         */
        String address = "http://guolin.tech/api/china";
        if ("city".equals(type)) {
            address += "/" + selectedProvince.getProvinceCode();
        } else if ("county".equals(type)) {
            address += "/" + selectedProvince.getProvinceCode()+"/" + selectedCity.getCityCode();
        }

        HttpUtil.sendHttpdRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Log.e("error","cannot load the url");
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getProvinceCode());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getCityCode());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "county":
                                    queryCounty();
                                    break;
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 查询省份信息
     */

    private void queryProvinces() {
        titleText.setText("中国");
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            data.clear();
            for (Province province : provinceList) {
                data.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer("province");
        }
    }

    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
//        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceCode = ?", String.valueOf(selectedProvince.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            data.clear();
            for (City city : cityList) {
                data.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer("city");
        }
    }

    private void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        countyList = DataSupport.where("cityCode = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size()>0) {
            data.clear();
            for (County county:countyList) {
                data.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer("county");
        }

    }

    /**
     * 显示对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
        Logger.d("123");
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
