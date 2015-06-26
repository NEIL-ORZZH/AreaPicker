package com.chenenyu.areapickerdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.chenenyu.areapicker.AreaPicker;
import com.chenenyu.areapicker.PickLevel;


public class MainActivity extends AppCompatActivity implements AreaPicker.OnAreaPickListener {
    private AreaPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAreaPicker();
            }
        });
    }

    private void showAreaPicker() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        picker = new AreaPicker();
        // 支持单级/省市/省市区联动
        //picker.setLevel(PickLevel.PROVINCE_CITY);
        ft.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out, R.anim.push_bottom_in, R.anim.push_bottom_out);
        picker.show(ft, "dialog");
    }

    @Override
    public void onAreaPick(final String[] area) {
        Toast.makeText(this, area[0] + area[1] + area[2], Toast.LENGTH_SHORT).show();
    }

}
