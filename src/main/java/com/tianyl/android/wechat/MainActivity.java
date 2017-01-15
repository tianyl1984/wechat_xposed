package com.tianyl.android.wechat;

import com.tianyl.android.wechat.sync.UploadService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Toast.makeText(MainActivity.this,"start...",Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UploadService.update();
                    }
                }).start();
            }
		});
	}
	
}
