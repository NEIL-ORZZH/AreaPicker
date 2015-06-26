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

package com.chenenyu.areapicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chenenyu.areapicker.R;
import com.chenenyu.areapicker.model.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cheney on 15/6/24 12:10.
 */
public class AreaAdapter extends BaseAdapter {
    private Context context;
    private List<Area> list;
    private ListView provinceLv, cityLv, countyLv;
    private int selectedItem = -1;

    public AreaAdapter(Context context, List<Area> list, ListView provinceLv, ListView cityLv, ListView countyLv) {
        this.context = context;
        if (list == null)
            this.list = new ArrayList<Area>();
        else
            this.list = list;
        this.provinceLv = provinceLv;
        this.cityLv = cityLv;
        this.countyLv = countyLv;
    }

    public void setData(List<Area> list) {
        if (list != null) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_area_picker, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_indicator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(position).getName());

        if (selectedItem == position) {
            holder.tv.setTextColor(0xffff5d54);
            holder.iv.setVisibility(View.VISIBLE);
        } else {
            holder.tv.setTextColor(0xff323232);
            holder.iv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    class ViewHolder {
        TextView tv;
        ImageView iv;
    }
}
