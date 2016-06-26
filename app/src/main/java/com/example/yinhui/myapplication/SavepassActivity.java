package com.example.yinhui.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SavepassActivity extends AppCompatActivity {

    String adminUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savepass);
        Intent intent = getIntent();
        adminUser = intent.getStringExtra("user");
        Button button1 = (Button) findViewById(R.id.savepass_save);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText website_et = (EditText) findViewById(R.id.savepass_website);
                EditText pass_et = (EditText) findViewById(R.id.savepass_pass);
                EditText pass_et2 = (EditText) findViewById(R.id.savepass_pass2);
                EditText account_et = (EditText) findViewById(R.id.savepass_account);

                String website = website_et.getText().toString();
                String pass = pass_et.getText().toString();
                String pass2 = pass_et2.getText().toString();
                String account = account_et.getText().toString();
                if(website == null || website.equals("")|| account == null || account.equals("") || pass == null || pass.equals("") || pass2 == null || !pass.equals(pass2)) {
                    Toast.makeText(SavepassActivity.this, "请输入有效的网站和密码!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        PasswordUtil.setPassByProperty(adminUser, account, website, pass);

                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                        String encoded_pass = PasswordUtil.encryptToStringDES(pass);
                        String url = "http://192.168.1.105:8080/DBWebService/rest/pass/" + adminUser + "/" + website + "/" + account + "/" + encoded_pass;
                        HttpURLConnection httpConn = null;
                        OutputStream out = null;
                        httpConn = (HttpURLConnection) new URL(url).openConnection();
                        httpConn.setRequestMethod("POST");
                        httpConn.connect();
                        int code = httpConn.getResponseCode();

                        Toast.makeText(SavepassActivity.this, "密码保存成功!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SavepassActivity.this, "密码保存出错!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

            }
        });

    }
}
