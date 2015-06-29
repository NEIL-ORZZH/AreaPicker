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

package com.chenenyu.areapicker.helper;

import android.content.Context;

/**
 * Created by Cheney on 15/6/24 11:41.
 */
public class AreaHelper {
    private String AREA = "AREA";
    private String CURRENT_PROVINCE = "CURRENT_PROVINCE";
    private String CURRENT_CITY = "CURRENT_CITY";
    private String CURRENT_COUNTY = "CURRENT_COUNTY";
    private Context context;

    public AreaHelper(Context context) {
        this.context = context;
    }

    public void saveCurrentProvince(String province) {
        SPUtil.put(context, AREA, CURRENT_PROVINCE, province);
    }

    public void saveCurrentCity(String city) {
        SPUtil.put(context, AREA, CURRENT_CITY, city);
    }

    public void saveCurrentCounty(String county) {
        SPUtil.put(context, AREA, CURRENT_COUNTY, county);
    }

    public String getProvince() {
        return (String)SPUtil.get(context, AREA, CURRENT_PROVINCE, "");
    }

    public String getCity() {
        return (String)SPUtil.get(context, AREA, CURRENT_CITY, "");
    }

    public String getCounty() {
        return (String)SPUtil.get(context, AREA, CURRENT_COUNTY, "");
    }

}
