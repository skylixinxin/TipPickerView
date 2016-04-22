package com.lixinxin.tippickerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TipPickerView tipPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tipPickerView = (TipPickerView) findViewById(R.id.tipPickerView);
        List<String> datas = new ArrayList<String>();
        for (int i = 0; i < 17; i++) {
            datas.add("0" + i);
        }
        tipPickerView.setmData(datas);
        tipPickerView.setmSelectListener(new TipPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                Toast.makeText(MainActivity.this, "选择了" + text, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
