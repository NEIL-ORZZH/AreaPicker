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

package com.chenenyu.areapicker.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chenenyu.areapicker.model.Area;
import com.chenenyu.areapicker.model.City;
import com.chenenyu.areapicker.model.County;
import com.chenenyu.areapicker.model.Province;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cheney on 15/6/23.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "areapicker.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PROVINCE = "province";
    private static final String TABLE_CITY = "city";
    private static final String TABLE_COUNTY = "county";
    private Context context;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table_province = "create table if not exists "
                + TABLE_PROVINCE
                + " (id integer primary key autoincrement, no varchar(10), code varchar(10), name varchar(60))";
        String create_table_city = "create table if not exists "
                + TABLE_CITY
                + " (id integer primary key autoincrement, no varchar(10), code varchar(10), name varchar(60), province_code varchar(10))";
        String create_table_county = "create table if not exists "
                + TABLE_COUNTY
                + " (id integer primary key autoincrement, no varchar(10), code varchar(10), name varchar(60), city_code varchar(10))";
        db.execSQL(create_table_province);
        db.execSQL(create_table_city);
        db.execSQL(create_table_county);
        initDB(db);
    }

    private void initDB(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            execAssetsSQL(db, "province.sql");
            execAssetsSQL(db, "city.sql");
            execAssetsSQL(db, "county.sql");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Execute SQL from text(.sql) in assets folder.
     *
     * @param db      SQLiteDatabase
     * @param sqlName sql's name
     */
    private void execAssetsSQL(SQLiteDatabase db, String sqlName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open(sqlName)));
            String line = "";
            //String buffer = "";
            while ((line = br.readLine()) != null) {
                //buffer += line;
                db.execSQL(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Query all the provinces.
     *
     * @return List<Province>
     */
    public List<Area> getProvinceList() {
        List list = new ArrayList();
        Province province;
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = {"code", "name"};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_PROVINCE, columns, null, null, null, null, " no asc ");
            while (cursor.moveToNext()) {
                if (!"".equals(cursor.getString(1))) {
                    province = new Province();
                    province.setCode(cursor.getString(0));
                    province.setName(cursor.getString(1));
                    list.add(province);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db.isOpen())
                db.close();
        }
        return list;
    }

    /**
     * Query all the cities according to the province_code.
     *
     * @param province_code province_code
     * @return List<City>
     */
    public List<Area> getCityList(String province_code) {
        List list = new ArrayList();
        City city;
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = {"code", "name"};
        String p_code[] = {province_code};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CITY, columns, " province_code = ? ", p_code, null, null, " no asc ");
            while (cursor.moveToNext()) {
                if (!"".equals(cursor.getString(1))) {
                    city = new City();
                    city.setCode(cursor.getString(0));
                    city.setName(cursor.getString(1));
                    list.add(city);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db.isOpen())
                db.close();
        }
        return list;
    }

    /**
     * Query all the cities according to the city_code.
     *
     * @param city_code city_code
     * @return List<County>
     */
    public List<Area> getCountyList(String city_code) {
        List<Area> list = new ArrayList();
        County county;
        SQLiteDatabase db = this.getReadableDatabase();
        String columns[] = {"code", "name"};
        String c_code[] = {city_code};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_COUNTY, columns, " city_code = ? ", c_code, null, null, " no asc ");
            while (cursor.moveToNext()) {
                if (!"".equals(cursor.getString(1))) {
                    county = new County();
                    county.setCode(cursor.getString(0));
                    county.setName(cursor.getString(1));
                    list.add(county);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (db.isOpen())
                db.close();
        }
        return list;
    }
}
