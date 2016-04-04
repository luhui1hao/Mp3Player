package com.example.luhui1hao.mp3player;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.luhui1hao.R;
import com.example.luhui1hao.application.MyApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luhui1hao on 2015/12/10.
 */
public class IPActivity extends Activity {
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ip);

        et = (EditText) findViewById(R.id.ip_et);

        //给按键绑定监听器
        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = null;
                temp = et.getText().toString();
                Pattern p = Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$");
                Matcher m = p.matcher(temp);
                if (m.matches()) {
                    MyApplication.setURL(temp);
                    Toast.makeText(IPActivity.this, MyApplication.BASE_URL, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(IPActivity.this, "请输入正确的IP地址", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
