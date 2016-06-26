package com.example.yinhui.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GetpassActivity extends AppCompatActivity {
    String adminUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpass);
        Intent intent = getIntent();
        adminUser = intent.getStringExtra("user");

        Button button1 = (Button) findViewById(R.id.getpass_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView pass_view = (TextView) findViewById(R.id.pass);
                EditText website_view = (EditText) findViewById(R.id.website_getpass);
                String website = website_view.getText().toString();

                PassPair[] account_pass = null;
                if(website == null || website == "") {
                    Toast.makeText(GetpassActivity.this, "请输入有效的网站!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        account_pass = PasswordUtil.getPassByProperty(adminUser, website);
                    } catch (Exception e) {
                        Toast.makeText(GetpassActivity.this, "密码获取出错!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                if(account_pass != null && account_pass.length > 0) {
                    String account_pass_string = "";
                    for(PassPair p: account_pass)
                        account_pass_string = account_pass_string + p.toString() + "\n";
                    pass_view.setText(account_pass_string);
                }
                else
                    Toast.makeText(GetpassActivity.this, "没有找到密码", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
