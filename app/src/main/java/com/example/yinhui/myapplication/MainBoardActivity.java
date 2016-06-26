package com.example.yinhui.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);
        Intent intent = getIntent();
        final String adminUser = intent.getStringExtra("user");
        Button button1 = (Button) findViewById(R.id.savepass);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainBoardActivity.this, SavepassActivity.class);
                intent.putExtra("user", adminUser);
                startActivity(intent);
            }
        });

        Button button2 = (Button) findViewById(R.id.getpass);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainBoardActivity.this, GetpassActivity.class);
                intent.putExtra("user", adminUser);
                startActivity(intent);
            }
        });
    }
}
