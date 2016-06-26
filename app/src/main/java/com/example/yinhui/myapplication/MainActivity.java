package com.example.yinhui.myapplication;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String passAdmin = "Qwer@1234";
        try {
            PasswordUtil.init(this, passAdmin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button button1 = (Button) findViewById(R.id.check_pass_mgr);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminUser = ((EditText) findViewById(R.id.user_mgr)).getText().toString();
                String queriedPass = PasswordUtil.getUserPass(adminUser);

                String input_pass = ((EditText) findViewById(R.id.pass_mgr)).getText().toString();
                input_pass = PasswordUtil.encryptToStringDES(input_pass);

                String pass_rest = null;
                try {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                    String url = "http://192.168.1.105:8080/DBWebService/rest/user/" + adminUser;
                    HttpURLConnection httpConn = null;
                    OutputStream out = null;
                    httpConn = (HttpURLConnection) new URL(url).openConnection();
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                 //   Scanner scanner = new Scanner(httpConn.getInputStream());
                 //   pass_rest = scanner.next();
                    InputStreamReader reader = new InputStreamReader(httpConn.getInputStream());
                    int b = 0;
                    StringBuffer sb=new StringBuffer();
                    while((b = reader.read()) != -1 ) {
                        sb.append((char)b);
                    }
                    pass_rest = sb.toString();
 //                   int len = reader.read();
 //                   char[] pass_chars = new char[len + 1];
 //                   reader.read(pass_chars, 0, len);
 //                   pass_rest = new String(pass_chars);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(input_pass != null && queriedPass != null && queriedPass.equals(input_pass) && pass_rest != null && pass_rest.equals(input_pass)) {
                    Intent intent = new Intent(MainActivity.this, MainBoardActivity.class);
                    intent.putExtra("user", adminUser);
                    startActivity(intent);
                } else
                    Toast.makeText(MainActivity.this, "请输入正确的管理密码!!", Toast.LENGTH_SHORT).show();
            }
        });

        Button button2 = (Button) findViewById(R.id.reg_pass_mgr);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminUser = ((EditText) findViewById(R.id.user_mgr)).getText().toString();
                String input_pass = ((EditText) findViewById(R.id.pass_mgr)).getText().toString();
                if(adminUser == null || input_pass == null || adminUser.indexOf(" ") >= 0 || input_pass.indexOf(" ") >= 0) {
                    Toast.makeText(MainActivity.this, "请输入正确的管理密码!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        PasswordUtil.createUser(adminUser, input_pass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                        String encoded_pass = PasswordUtil.encryptToStringDES(input_pass);
                        String url = "http://192.168.1.105:8080/DBWebService/rest/user/" + adminUser + "/" + URLEncoder.encode(encoded_pass);
                        HttpURLConnection httpConn = null;
                        OutputStream out = null;
                        httpConn = (HttpURLConnection) new URL(url).openConnection();
                        httpConn.setRequestMethod("POST");
                        httpConn.connect();
                        int code = httpConn.getResponseCode();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
