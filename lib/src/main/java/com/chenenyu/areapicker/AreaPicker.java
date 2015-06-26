/*
 * Copyright (C) 2015 chenenyu
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chenenyu.areapicker;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chenenyu.areapicker.adapter.AreaAdapter;
import com.chenenyu.areapicker.db.DBHelper;
import com.chenenyu.areapicker.helper.AreaHelper;
import com.chenenyu.areapicker.model.Area;
import com.chenenyu.areapicker.model.City;
import com.chenenyu.areapicker.model.County;
import com.chenenyu.areapicker.model.Province;

/**
 * Created by Cheney on 15/6/23.
 */
public class AreaPicker extends DialogFragment implements OnClickListener {

    private FragmentActivity mActivity;
    private Handler handler;
    /**
     * View
     */
    private View mContentView;
    private ImageView iv_back, iv_cancel;
    private TextView tv_title;
    private ListView provinceLv, cityLv, countyLv;
    /**
     * Data
     */
    private AreaHelper areaHelper;
    private DBHelper db;
    private String selectedProvince = "", selectedCity = "", selectedCounty = "";
    private List<Area> province;
    private List<Area> city;
    private List<Area> county;
    private AreaAdapter provinceAdapter, cityAdapter, countyAdapter;
    /**
     * 选择模式
     */
    private PickLevel pickLevel;
    /**
     * 过渡延迟
     */
    private long transDelay = 200;

    /**
     * Callback
     */
    private OnAreaPickListener mCallback;

    public interface OnAreaPickListener {
        void onAreaPick(String[] area);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (FragmentActivity) activity;
        try {
            mCallback = (OnAreaPickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAreaPickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = LayoutInflater.from(mActivity).inflate(R.layout.area_picker_layout, container);
        handler = new Handler();
        initView();
        initData();
        return mContentView;
    }

    private void initView() {
        iv_back = (ImageView) mContentView.findViewById(R.id.iv_back);
        iv_cancel = (ImageView) mContentView.findViewById(R.id.iv_cancel);
        tv_title = (TextView) mContentView.findViewById(R.id.tv_title);
        tv_title.setText(R.string.choose_province);
        iv_back.setOnClickListener(this);
        iv_cancel.setOnClickListener(this);

        provinceLv = (ListView) mContentView.findViewById(R.id.lv_province);
        cityLv = (ListView) mContentView.findViewById(R.id.lv_city);
        countyLv = (ListView) mContentView.findViewById(R.id.lv_county);
    }

    private void initData() {
        // 获取所有省份
        db = new DBHelper(mActivity);
        province = db.getProvinceList();

        provinceAdapter = new AreaAdapter(mActivity, province, provinceLv, cityLv, countyLv);
        // 获取已选择的省份并标记
        provinceAdapter.setSelectedItem(getSavedProvincePosition());
        provinceLv.setAdapter(provinceAdapter);
        provinceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 更新当前省份标记状态并保存
                Province current_province = (Province) parent.getItemAtPosition(position);
                selectedProvince = current_province.getName();
                areaHelper.saveCurrentProvince(selectedProvince);
                provinceAdapter.setSelectedItem(position);
                provinceAdapter.notifyDataSetChanged();
                if (getLevel() == PickLevel.PROVINCE_ONLY) {
                    submit();
                } else {
                    // 设置城市数据
                    setCityData(current_province.getCode());
                }
            }
        });
        cityAdapter = new AreaAdapter(mActivity, city, provinceLv, cityLv, countyLv);
        cityLv.setAdapter(cityAdapter);
        cityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City current_city = (City) parent.getItemAtPosition(position);
                selectedCity = current_city.getName();
                areaHelper.saveCurrentCity(selectedCity);
                cityAdapter.setSelectedItem(position);
                cityAdapter.notifyDataSetChanged();
                if (getLevel() == PickLevel.PROVINCE_CITY) {
                    submit();
                } else {
                    setCountyData(current_city.getCode());
                }
            }
        });
        countyAdapter = new AreaAdapter(mActivity, county, provinceLv, cityLv, countyLv);
        countyLv.setAdapter(countyAdapter);
        countyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                County current_county = (County) parent.getItemAtPosition(position);
                selectedCounty = current_county.getName();
                areaHelper.saveCurrentCounty(selectedCounty);
                countyAdapter.setSelectedItem(position);
                countyAdapter.notifyDataSetChanged();
                submit();
            }
        });
    }

    /**
     * 获取选择模式
     *
     * @return PickLevel
     */
    public PickLevel getLevel() {
        return pickLevel;
    }

    /**
     * 设置选择模式
     *
     * @param pickLevel PickLevel
     */
    public void setLevel(PickLevel pickLevel) {
        this.pickLevel = pickLevel;
    }

    /**
     * 获取被选择的省份Position
     *
     * @return int
     */
    private int getSavedProvincePosition() {
        areaHelper = new AreaHelper(mActivity);
        selectedProvince = areaHelper.getProvince();
        for (int i = 0; i < province.size(); i++) {
            if (selectedProvince.equals(province.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取被选择的城市Position
     *
     * @return int
     */
    private int getSavedCityPosition() {
        areaHelper = new AreaHelper(mActivity);
        selectedCity = areaHelper.getCity();
        for (int i = 0; i < city.size(); i++) {
            if (selectedCity.equals(city.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取被选择的县区Position
     *
     * @return int
     */
    private int getSavedCountyPosition() {
        areaHelper = new AreaHelper(mActivity);
        selectedCounty = areaHelper.getCounty();
        for (int i = 0; i < county.size(); i++) {
            if (selectedCounty.equals(county.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 设置城市的数据
     *
     * @param province_code 关联的省份code
     */
    private void setCityData(String province_code) {
        city = db.getCityList(province_code);
        if (city != null) {
            cityAdapter.setSelectedItem(getSavedCityPosition());
        }
        cityAdapter.setData(city);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_title.setText(R.string.choose_city);
                iv_back.setVisibility(View.VISIBLE);
                provinceLv.setVisibility(View.GONE);
                cityLv.setVisibility(View.VISIBLE);
                countyLv.setVisibility(View.GONE);
            }
        }, transDelay);
    }

    /**
     * 设置县区的数据
     *
     * @param city_code 关联的城市code
     */
    private void setCountyData(String city_code) {
        county = db.getCountyList(city_code);
        if (county != null) {
            countyAdapter.setSelectedItem(getSavedCountyPosition());
        }
        countyAdapter.setData(county);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_title.setText(R.string.choose_county);
                provinceLv.setVisibility(View.GONE);
                cityLv.setVisibility(View.GONE);
                countyLv.setVisibility(View.VISIBLE);
            }
        }, transDelay);
    }

    /**
     * 提交选择,调用回调方法传值给Activity
     */
    private void submit() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String[] result = {selectedProvince, selectedCity, selectedCounty};
                mCallback.onAreaPick(result);
                close();
            }
        }, transDelay);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            handleBack();
        } else if (id == R.id.iv_cancel) {
            close();
        }
    }

    /**
     * 回退操作
     */
    private void handleBack() {
        if (countyLv.getVisibility() == View.VISIBLE) {
            tv_title.setText("选择城市");
            provinceLv.setVisibility(View.GONE);
            cityLv.setVisibility(View.VISIBLE);
            countyLv.setVisibility(View.GONE);
        } else if (cityLv.getVisibility() == View.VISIBLE) {
            tv_title.setText("选择省份");
            iv_back.setVisibility(View.INVISIBLE);
            provinceLv.setVisibility(View.VISIBLE);
            cityLv.setVisibility(View.GONE);
            countyLv.setVisibility(View.GONE);
        } else {
            close();
        }
    }

    private void close() {
        AreaPicker.this.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null)
            db.close();
    }
}